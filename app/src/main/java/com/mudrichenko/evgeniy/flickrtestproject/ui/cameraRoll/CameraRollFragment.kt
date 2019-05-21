package com.mudrichenko.evgeniy.flickrtestproject.ui.cameraRoll

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.mudrichenko.evgeniy.flickrtestproject.EndlessScrollListener
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.photoFullscreen.PhotoFullscreenFragment
import com.todddavies.components.progressbar.ProgressWheel
import java.util.ArrayList

class CameraRollFragment: BaseFragment(), CameraRollView,
        PhotosRecyclerViewAdapter.OnItemClickListener,
        PhotoFullscreenFragment.PhotoFullscreenFragmentListener,
        SwipeRefreshLayout.OnRefreshListener {

    @InjectPresenter
    lateinit var mCameraRollPresenter: CameraRollPresenter

    private lateinit var mMainLayout: FrameLayout

    private lateinit var mProgressWheel: ProgressWheel

    private lateinit var mRecyclerViewItems: ArrayList<RecyclerViewItem>

    private lateinit var mPhotosAdapter: PhotosRecyclerViewAdapter

    private lateinit var mTextViewInfo: TextView

    private lateinit var mPhotosRecyclerView: RecyclerView

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private lateinit var mScrollListener: EndlessScrollListener

    private val NUM_OF_COLUMNS = 2

    companion object {
        fun newInstance() = CameraRollFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_camera_roll, container, false)
        mMainLayout = view.findViewById(R.id.main_layout)
        mProgressWheel = view.findViewById(R.id.progress_wheel)
        mProgressWheel.visibility = View.INVISIBLE
        mTextViewInfo = view.findViewById(R.id.text_view_info)
        // recyclerView elements
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mRecyclerViewItems = ArrayList()
        mPhotosAdapter = PhotosRecyclerViewAdapter(context!!, mRecyclerViewItems)
        mPhotosAdapter.setOnItemClickListener(this)
        mPhotosRecyclerView = view.findViewById(R.id.recycler_view_photos)
        mPhotosRecyclerView.adapter = mPhotosAdapter
        val gridLayoutManager = GridLayoutManager(context, NUM_OF_COLUMNS)
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
                mCameraRollPresenter.listBottomReached()
            }

            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                mCameraRollPresenter.loadMorePhotos()
            }
        }
        mPhotosRecyclerView.addOnScrollListener(mScrollListener)
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
            mCameraRollPresenter.clickedOnPhoto(position)
        }
    }

    override fun onPhotoDeleted() {
        mRecyclerViewItems.removeAt(mCameraRollPresenter.getFullscreenPhotoIndex())
        mPhotosAdapter.notifyDataSetChanged()
    }

    override fun onRefresh() {
        mCameraRollPresenter.loadFirstPage()
    }

    private fun showPhotoFullscreen(flickrPhoto: FlickrPhoto?) {
        val photoFullscreenFragment = PhotoFullscreenFragment.newInstance(flickrPhoto)
        photoFullscreenFragment.showFragment(activity, R.id.main_menu_layout, false)
        photoFullscreenFragment.setPhotoFullscreenFragmentListener(this)
    }

    fun setBackgroundColor(color: Int) {
        mMainLayout.setBackgroundColor(color)
    }

}