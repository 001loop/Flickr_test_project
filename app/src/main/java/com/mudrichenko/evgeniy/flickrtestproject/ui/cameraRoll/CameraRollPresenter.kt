package com.mudrichenko.evgeniy.flickrtestproject.ui.cameraRoll

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.AppConstants
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.BasePhotosRepository
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.CameraRollPhotosRepository
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils
import java.util.ArrayList
import javax.inject.Inject

@InjectViewState
class CameraRollPresenter: MvpPresenter<CameraRollView>(), BasePhotosRepository.RepositoryListener {

    @Inject
    lateinit var mNetworkUtils: NetworkUtils

    @Inject
    lateinit var mAuthUtils: AuthUtils

    @Inject
    lateinit var mCameraRollRepository: CameraRollPhotosRepository

    private var mFlickrPhotoList: List<FlickrPhoto>? = null

    private var mCurrentPage: Int = 0

    private var isPageLoaded: Boolean = false

    private var isLastPageReached: Boolean = false

    private var mFullscreenPhotoIndex: Int = 0

    private var mIsInternetConnectionAvailable: Boolean = false

    private var mIsListRefreshed: Boolean = false

    override fun onFirstViewAttach() {
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

    private fun onPhotoListLoad() {
        mCameraRollRepository.setRepositoryListener(this)
        mCameraRollRepository.startLoadPhotosListTask(mCurrentPage, mAuthUtils.getUserPhotosUrl(mCurrentPage, AppConstants.NUM_OF_PHOTOS_ON_PAGE, null))
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
        mCameraRollRepository.unsubscribe()
    }

    override fun onInternetConnectionProblemNotify() {
        mIsInternetConnectionAvailable = false
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