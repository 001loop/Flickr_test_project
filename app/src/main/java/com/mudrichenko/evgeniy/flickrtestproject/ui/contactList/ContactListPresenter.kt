package com.mudrichenko.evgeniy.flickrtestproject.ui.contactList

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.AppConstants
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.BasePhotosRepository
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.ContactListPhotosRepository
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.ContactListRepository
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrContact
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils
import com.orhanobut.logger.Logger

import java.util.ArrayList

import javax.inject.Inject

@InjectViewState
class ContactListPresenter : MvpPresenter<ContactListView>(),
        ContactListRepository.ContactListRepositoryListener,
        BasePhotosRepository.RepositoryListener {

    @Inject
    lateinit var mNetworkUtils: NetworkUtils

    @Inject
    lateinit var mAuthUtils: AuthUtils

    @Inject
    lateinit var mContactListRepository: ContactListRepository

    @Inject
    lateinit var mContactListPhotosRepository: ContactListPhotosRepository

    private var mFlickrPhotoList: List<FlickrPhoto>? = null

    private var mCurrentPage: Int = 0

    private var isPageLoaded: Boolean = false

    private var isLastPageReached: Boolean = false

    private var mFullscreenPhotoIndex: Int = 0

    private var mIsInternetConnectionAvailable: Boolean = false

    private var mIsListRefreshed: Boolean = false


    internal var mContactList: List<FlickrContact>? = null

    private var mCurrentUserId: String? = null

    override fun onFirstViewAttach() {
        loadContactList()
        mIsListRefreshed = false
        mIsInternetConnectionAvailable = mNetworkUtils.isInternetConnectionAvailable()
        loadFirstPage(true)
    }

    fun loadFirstPage(isNeedToShowProgressWheel: Boolean) {
        viewState.resetPhotoList()
        if (isNeedToShowProgressWheel) {
            viewState.showProgressWheel()
        }
        mCurrentPage = 1
        isPageLoaded = false
        isLastPageReached = false
        onPhotoListLoad()
    }

    init {
        App.appComponent!!.inject(this)
    }

    private fun loadContactList() {
        mContactListRepository.setContactListRepositoryListener(this)
        mContactListRepository.startLoadContactListTask()
        mContactListPhotosRepository.setRepositoryListener(this)
    }

    fun onContactPicked(index: Int) {
        if (mContactList == null) {
            return
        }
        if (mContactList!!.size < index) {
            return
        }
        viewState.resetPhotoList()
        mCurrentPage = 1
        val (nsid, username) = mContactList!![index]
        mCurrentUserId = nsid
        Logger.i("onContactPicked = $username")
        isPageLoaded = true
        loadFirstPage(true)
    }

    fun loadMorePhotos() {
        if (isLastPageReached) {
            return
        }
        if (isPageLoaded) {
            mCurrentPage++
            onPhotoListLoad()
        }
    }

    fun listBottomReached() {
        if (!isLastPageReached && mIsInternetConnectionAvailable) {
            viewState.showRefreshWheel()
        }
    }

    fun clickedOnPhoto(index: Int) {
        mFullscreenPhotoIndex = index
        viewState.showPhotoFullscreenFragment(mFullscreenPhotoIndex)
    }

    fun getFullscreenPhotoIndex(): Int {
        return mFullscreenPhotoIndex
    }

    fun unsubscribe() {
        mContactListPhotosRepository.unsubscribe()
        mContactListRepository.unsubscribe()
    }

    override fun onInternetConnectionProblemNotify() {
        mIsInternetConnectionAvailable = false
    }


    private fun onPhotoListLoad() {
        mContactListPhotosRepository.setRepositoryListener(this)
        mContactListPhotosRepository.startLoadPhotosListTask(mCurrentPage, mAuthUtils.getUserPhotosUrl(mCurrentPage, AppConstants.NUM_OF_PHOTOS_ON_PAGE, mCurrentUserId))
    }

    override fun onContactListReceived(contactList: List<FlickrContact>) {
        mContactList = contactList
        if (mContactList!!.isEmpty()) {
            viewState.showInfoMessage(App.appContext!!.getString(R.string.contact_no_contacts))
            return
        }
        val contactNames = ArrayList<String>()
        for (x in mContactList!!.indices) {
            contactNames.add(mContactList!![x].username)
        }
        viewState.onContactListLoaded(contactNames)
    }

    override fun onPhotoListUploadingFinishedListener(flickrPhotoList: List<FlickrPhoto>) {
        mFlickrPhotoList = flickrPhotoList
        isPageLoaded = true
        val recyclerViewItems = ArrayList<RecyclerViewItem>()
        for (x in flickrPhotoList.indices) {
            recyclerViewItems.add(RecyclerViewItem(PhotosRecyclerViewAdapter.viewTypeItem, flickrPhotoList[x]))
        }
        if (flickrPhotoList.isNotEmpty() && flickrPhotoList[0].page == mCurrentPage) {
            viewState.showPhotos(recyclerViewItems)
        }
        hideProgressWheels()
        if (isLastPageReached) {
            viewState.lastPageReached()
        }
    }

    override fun onLastPageReached() {
        isLastPageReached = true
    }

    override fun onErrorReceived(errorMessage: String) {
        isLastPageReached = true
        val flickrPhotoList = mFlickrPhotoList
        if (flickrPhotoList == null || flickrPhotoList.isEmpty()) {
            viewState.showInfoMessage(errorMessage)
        }
        hideProgressWheels()
        if (mIsListRefreshed) {
            mIsInternetConnectionAvailable = mNetworkUtils.isInternetConnectionAvailable()
            if (mIsInternetConnectionAvailable) {
                viewState.showSnackbarMessage(errorMessage)
            }
        }
    }

    private fun hideProgressWheels() {
        viewState.hideProgressWheel()
        viewState.hideRefreshWheel()
    }

}
