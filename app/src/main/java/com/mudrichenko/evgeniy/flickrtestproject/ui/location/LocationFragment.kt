package com.mudrichenko.evgeniy.flickrtestproject.ui.location

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.material.snackbar.Snackbar
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.EndlessScrollListener
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.map.MapFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.photoFullscreen.PhotoFullscreenFragment
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils
import com.orhanobut.logger.Logger
import com.todddavies.components.progressbar.ProgressWheel

import java.util.ArrayList

import javax.inject.Inject

class LocationFragment : BaseFragment(), LocationView,
        PhotosRecyclerViewAdapter.OnItemClickListener,
        PhotoFullscreenFragment.PhotoFullscreenFragmentListener,
        SwipeRefreshLayout.OnRefreshListener, MapFragment.MapFragmentListener, View.OnClickListener {

    @InjectPresenter
    lateinit var mLocationPresenter: LocationPresenter

    @Inject
    lateinit var mPrefUtils: PrefUtils

    lateinit var mMapFragment: MapFragment

    private lateinit var mMainLayout: RelativeLayout

    private lateinit var mProgressWheel: ProgressWheel

    private lateinit var mRecyclerViewItems: ArrayList<RecyclerViewItem>

    private lateinit var mPhotosAdapter: PhotosRecyclerViewAdapter

    private lateinit var mTextViewInfo: TextView

    private lateinit var mPhotosRecyclerView: RecyclerView

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private lateinit var mScrollListener: EndlessScrollListener

    private val NUM_OF_COLUMNS = 2

    private lateinit var mBtnSetLocation: Button

    private lateinit var mTextViewLocationName: TextView

    private var mMapFragmentTag = "mapFragment"

    companion object {
        fun newInstance() = LocationFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_location, container, false)
        App.appComponent!!.inject(this)
        val mContext = context
        mMainLayout = view.findViewById(R.id.main_layout)
        // progress wheel
        mProgressWheel = view.findViewById(R.id.progress_wheel)
        mProgressWheel.visibility = View.INVISIBLE
        mProgressWheel.setText(resources.getString(R.string.progress_bar_loading))
        mProgressWheel.textSize = resources.getDimensionPixelOffset(R.dimen.wheelProgressBarTextSize)
        if (mContext != null) {
            mProgressWheel.textColor = ContextCompat.getColor(mContext, R.color.wheelProgressBarText)
        }
        mTextViewInfo = view.findViewById(R.id.text_view_info)
        // recyclerView elements
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mRecyclerViewItems = ArrayList()
        if (mContext != null) {
            mPhotosAdapter = PhotosRecyclerViewAdapter(mContext, mRecyclerViewItems)
        }
        mPhotosAdapter.setOnItemClickListener(this)
        mPhotosRecyclerView = view.findViewById(R.id.recycler_view_photos)
        mPhotosRecyclerView.adapter = mPhotosAdapter
        val gridLayoutManager = GridLayoutManager(mContext, NUM_OF_COLUMNS)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (mRecyclerViewItems[position].viewTypeId == PhotosRecyclerViewAdapter.viewTypeBottomText) {
                    NUM_OF_COLUMNS
                } else 1
            }
        }
        mPhotosRecyclerView.layoutManager = gridLayoutManager
        mScrollListener = object : EndlessScrollListener(gridLayoutManager) {
            override fun onBottomReached() {
                mLocationPresenter.listBottomReached()
            }

            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                mLocationPresenter.loadMorePhotos()
            }
        }
        mPhotosRecyclerView.addOnScrollListener(mScrollListener)
        // map
        mBtnSetLocation = view.findViewById(R.id.btn_set_location)
        mBtnSetLocation.setOnClickListener(this)
        mTextViewLocationName = view.findViewById(R.id.text_view_location_name)
        mTextViewLocationName.text = mPrefUtils.getLocationName()
        if (savedInstanceState != null) {
            val fragment = activity!!.supportFragmentManager.findFragmentByTag(mMapFragmentTag)
            if (fragment is MapFragment) {
                Logger.i("Info; restore fragment and listeners")
                mMapFragment = fragment
                mMapFragment.setMapFragmentListener(this)
            }
        }
        return view
    }

    override fun showProgressWheel() {
        mProgressWheel.visibility = View.VISIBLE
        mProgressWheel.startSpinning()
    }

    override fun hideProgressWheel() {
        mProgressWheel.visibility = View.INVISIBLE
        mProgressWheel.stopSpinning()
    }

    override fun showRefreshWheel() {
        mSwipeRefreshLayout.isRefreshing = true
    }

    override fun hideRefreshWheel() {
        mSwipeRefreshLayout.isRefreshing = false
    }

    override fun showInfoMessage(message: String) {
        hideProgressWheel()
        mTextViewInfo.visibility = View.VISIBLE
        mTextViewInfo.text = message
    }

    override fun showSnackbarMessage(message: String) {
        Snackbar.make(mMainLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun hideInfoMessage() {
        mTextViewInfo.visibility = View.INVISIBLE
    }

    override fun showPhotos(recyclerViewItems: List<RecyclerViewItem>) {
        mRecyclerViewItems.addAll(recyclerViewItems)
        mPhotosAdapter.notifyDataSetChanged()
        hideInfoMessage()
        hideRefreshWheel()
    }

    override fun resetPhotoList() {
        mRecyclerViewItems.clear()
        mPhotosAdapter.notifyDataSetChanged()
        mScrollListener.resetState()
    }

    override fun showPhotoFullscreenFragment(index: Int) {
        showPhotoFullscreen(mRecyclerViewItems[index].flickrPhoto)
    }

    override fun lastPageReached() {
        mRecyclerViewItems.add(RecyclerViewItem(PhotosRecyclerViewAdapter.viewTypeBottomText, null))
        mPhotosAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int) {
        if (mRecyclerViewItems.size > position) {
            mLocationPresenter.clickedOnPhoto(position)
        }
    }

    override fun onPhotoDeleted() {
        mRecyclerViewItems.removeAt(mLocationPresenter.getFullscreenPhotoIndex())
        mPhotosAdapter.notifyDataSetChanged()
    }

    override fun onRefresh() {
        mLocationPresenter.loadFirstPage(false)
    }

    private fun showPhotoFullscreen(flickrPhoto: FlickrPhoto?) {
        val photoFullscreenFragment = PhotoFullscreenFragment.newInstance(flickrPhoto)
        photoFullscreenFragment.showFragment(activity, R.id.fullscreen_fragment_container, false)
        photoFullscreenFragment.setPhotoFullscreenFragmentListener(this)
    }

    override fun setLocationName(locationName: String) {
        Logger.i("LocationFragment; setLocationName")
        mTextViewLocationName.text = locationName
    }

    private fun clickOnSetLocation() {
        showMapFragment()
    }

    private fun showMapFragment() {
        mMapFragment = MapFragment.newInstance(mLocationPresenter.latLng)
        mMapFragment.showFragment(activity, R.id.fullscreen_fragment_container, false, mMapFragmentTag)
        mMapFragment.setMapFragmentListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_set_location -> clickOnSetLocation()
        }
    }

    override fun onCoordinatesSelectedListener(lat: Double, lng: Double, locationName: String) {
        resetPhotoList()
        mLocationPresenter.onCoordinatesSelected(lat, lng, locationName)
    }

    override fun onCancelClickListener() {
        mLocationPresenter.onMapCancelClicked()
    }

}
