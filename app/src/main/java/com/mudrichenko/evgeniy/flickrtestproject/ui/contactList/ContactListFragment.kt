package com.mudrichenko.evgeniy.flickrtestproject.ui.contactList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.material.snackbar.Snackbar
import com.mudrichenko.evgeniy.flickrtestproject.EndlessScrollListener
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.photoFullscreen.PhotoFullscreenFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.publicPhotos.PublicPhotosFragment
import com.mudrichenko.evgeniy.flickrtestproject.ui.publicPhotos.PublicPhotosPresenter
import com.orhanobut.logger.Logger
import com.todddavies.components.progressbar.ProgressWheel
import com.toptoche.searchablespinnerlibrary.SearchableSpinner

import java.util.ArrayList

import java.security.AccessController.getContext

class ContactListFragment : BaseFragment(), ContactListView,
        PhotosRecyclerViewAdapter.OnItemClickListener,
        PhotoFullscreenFragment.PhotoFullscreenFragmentListener,
        SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {

    @InjectPresenter
    lateinit var mContactListPresenter: ContactListPresenter

    private lateinit var mMainLayout: RelativeLayout

    private lateinit var mProgressWheel: ProgressWheel

    private lateinit var mRecyclerViewItems: ArrayList<RecyclerViewItem>

    private lateinit var mPhotosAdapter: PhotosRecyclerViewAdapter

    private lateinit var mTextViewInfo: TextView

    private lateinit var mPhotosRecyclerView: RecyclerView

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private lateinit var mScrollListener: EndlessScrollListener

    private lateinit var mContacts: ArrayList<String>

    private lateinit var mContactsSpinnerAdapter: ArrayAdapter<String>

    private lateinit var mSpinner: SearchableSpinner

    private val NUM_OF_COLUMNS = 2

    companion object {
        fun newInstance() = ContactListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)
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
                mContactListPresenter.listBottomReached()
            }

            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                mContactListPresenter.loadMorePhotos()
            }
        }
        mPhotosRecyclerView.addOnScrollListener(mScrollListener)
        // contacts
        mSpinner = view.findViewById(R.id.spinner)
        mSpinner.setTitle(getString(R.string.contact_list_spinner_title))
        mContacts = ArrayList()
        mContactsSpinnerAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, mContacts)
        mSpinner.adapter = mContactsSpinnerAdapter
        mSpinner.onItemSelectedListener = this
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
            mContactListPresenter.clickedOnPhoto(position)
        }
    }

    override fun onPhotoDeleted() {
        mRecyclerViewItems.removeAt(mContactListPresenter.getFullscreenPhotoIndex())
        mPhotosAdapter.notifyDataSetChanged()
    }

    override fun onRefresh() {
        mContactListPresenter.loadFirstPage(false)
    }

    private fun showPhotoFullscreen(flickrPhoto: FlickrPhoto?) {
        val photoFullscreenFragment = PhotoFullscreenFragment.newInstance(flickrPhoto)
        photoFullscreenFragment.showFragment(activity, R.id.fullscreen_fragment_container, false)
        photoFullscreenFragment.setPhotoFullscreenFragmentListener(this)
    }

    fun setBackgroundColor(color: Int) {
        mMainLayout.setBackgroundColor(color)
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, index: Int, l: Long) {
        mContactListPresenter.onContactPicked(index)
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mContactListPresenter.unsubscribe()
    }

    override fun onContactListLoaded(contacts: ArrayList<String>) {
        mContacts.addAll(contacts)
        mContactsSpinnerAdapter.notifyDataSetChanged()
        mSpinner.setSelection(0)
    }

}
