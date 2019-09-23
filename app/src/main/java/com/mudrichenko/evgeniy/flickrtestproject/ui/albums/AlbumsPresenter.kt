package com.mudrichenko.evgeniy.flickrtestproject.ui.albums

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.AppConstants
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.AlbumsPhotosRepository
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.AlbumsRepository
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.BasePhotosRepository
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.CameraRollPhotosRepository
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhotoset
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils
import com.orhanobut.logger.Logger

import java.util.ArrayList

import javax.inject.Inject

@InjectViewState
class AlbumsPresenter : MvpPresenter<AlbumsView>(), AlbumsRepository.AlbumsRepositoryListener,
        BasePhotosRepository.RepositoryListener {

    @Inject
    lateinit var mNetworkUtils: NetworkUtils

    @Inject
    lateinit var mAuthUtils: AuthUtils

    @Inject
    lateinit var mAlbumsRepository: AlbumsRepository

    @Inject
    lateinit var mAlbumsPhotosRepository: AlbumsPhotosRepository

    private var mFlickrPhotoList: List<FlickrPhoto>? = null

    internal var mPhotosetList: List<FlickrPhotoset>? = null

    private var mCurrentPage: Int = 0

    private var isPageLoaded: Boolean = false

    private var isLastPageReached: Boolean = false

    private var mFullscreenPhotoIndex: Int = 0

    private var mIsInternetConnectionAvailable: Boolean = false

    private var mIsListRefreshed: Boolean = false

    private var mCurrentUserId: String? = null

    private var mCurrentAlbumId: Long = 0

    private val selectedAlbumIndex = 0

    override fun onFirstViewAttach() {
        loadPhotosetList()
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

    private fun loadPhotosetList() {
        mAlbumsRepository.setAlbumsRepositoryListener(this)
        mAlbumsRepository.startLoadPhotosetsListTask()
        mAlbumsPhotosRepository.setRepositoryListener(this)
    }

    fun onPhotosetPicked(index: Int) {
        if (mPhotosetList == null) {
            return
        }
        if (mPhotosetList!!.size < index) {
            return
        }
        viewState.resetPhotoList()
        mCurrentPage = 1
        val (id, ownerId, _, _, _, _, _, title) = mPhotosetList!![index]
        mCurrentAlbumId = id
        mCurrentUserId = ownerId
        Logger.i("onAlbumPicked = $title")
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
        mAlbumsPhotosRepository.unsubscribe()
        mAlbumsRepository.unsubscribe()
    }

    override fun onInternetConnectionProblemNotify() {
        mIsInternetConnectionAvailable = false
    }

    private fun onPhotoListLoad() {
        mAlbumsPhotosRepository.setRepositoryListener(this)
        mAlbumsPhotosRepository.startLoadPhotosListTask(mCurrentPage, mCurrentUserId!!, mCurrentAlbumId)
    }

    override fun onAlbumsListReceived(albumsList: List<FlickrPhotoset>) {
        mPhotosetList = albumsList
        if (mPhotosetList!!.isEmpty()) {
            viewState.showInfoMessage(App.appContext!!.getString(R.string.album_no_albums))
            return
        }
        val albumTitles = ArrayList<String>()
        for (x in mPhotosetList!!.indices) {
            albumTitles.add(mPhotosetList!![x].title)
        }
        viewState.onPhotosetListLoaded(albumTitles)
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

    /*
    @Override
    public void onPhotosetListUploadingFinishedListener(List<FlickrPhotoset> flickrPhotosetList) {
        boolean isPhotosetAvailable = false;
        if (flickrPhotosetList != null) {
            if (flickrPhotosetList.size() > 0) {
                isPhotosetAvailable = true;
            }
        }
        if (isPhotosetAvailable) {
            getViewState().showAlbumsSpinner();
            ArrayList<String> albumTitles = new ArrayList<>();
            for (int x = 0; x < flickrPhotosetList.size(); x ++) {
                mPhotosetList.add(flickrPhotosetList.get(x));
                albumTitles.add(flickrPhotosetList.get(x).getTitle());
            }
            getViewState().showAlbumsNames(albumTitles);
            // select album and download photos
            getViewState().selectSpinnerItem(selectedAlbumIndex);
        } else {
            getViewState().showInfoMessage(App.Companion.getAppContext().getString(R.string.album_no_albums));
            getViewState().hideProgressWheel();
            getViewState().hideAlbumsSpinner();
        }
    }
    */

    /*
    private void startUploadingPhotos() {
        getViewState().showProgressWheel();
        mAlbumsRepository.startLoadPhotosListTask(mCurrentPage, mUserId, mAlbumId);
    }

    public void onAlbumSelected(int index) {
        getViewState().resetPhotoList();
        mCurrentPage = 1;
        selectedAlbumIndex = index;
        if (mPhotosetList.size() < index) {
            return;
        }
        mUserId = mPhotosetList.get(index).getOwnerId();
        mAlbumId = mPhotosetList.get(index).getId();
        startUploadingPhotos();
    }
    */

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
