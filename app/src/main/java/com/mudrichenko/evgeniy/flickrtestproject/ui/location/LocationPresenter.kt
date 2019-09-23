package com.mudrichenko.evgeniy.flickrtestproject.ui.location

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.AppConstants
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.BasePhotosRepository
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.LocationPhotosRepository
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils
import com.google.android.gms.maps.model.LatLng
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils
import com.orhanobut.logger.Logger

import java.util.ArrayList

import javax.inject.Inject

@InjectViewState
class LocationPresenter : MvpPresenter<LocationView>(), BasePhotosRepository.RepositoryListener {

    @Inject
    lateinit var mNetworkUtils: NetworkUtils

    @Inject
    lateinit var mAuthUtils: AuthUtils

    @Inject
    lateinit var mPrefUtils: PrefUtils

    @Inject
    lateinit var mLocationPhotosRepository: LocationPhotosRepository

    private var mFlickrPhotoList: List<FlickrPhoto>? = null

    private var mCurrentPage: Int = 0

    private var isPageLoaded: Boolean = false

    private var isLastPageReached: Boolean = false

    private var mFullscreenPhotoIndex: Int = 0

    private var mIsInternetConnectionAvailable: Boolean = false

    private var mIsListRefreshed: Boolean = false

    private val LOCATION_CODE_NOT_SELECTED = "0"

    var latLng: LatLng? = null
        private set

    private var mLocationName: String? = null

    private var mIsFirstEntry: Boolean = false

    override fun onFirstViewAttach() {
        mIsListRefreshed = false
        mIsInternetConnectionAvailable = mNetworkUtils.isInternetConnectionAvailable()
        loadFirstEntryData()
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

    fun loadFirstEntryData() {
        Logger.i("onFirstViewAttach")
        Logger.i("firstAttach; num of views = " + attachedViews.size)
        mCurrentPage = 1
        isPageLoaded = false
        loadData()
        if (mIsFirstEntry) {
            Logger.i("mIsFirstEntry")
            viewState.showInfoMessage(App.appContext!!.getString(R.string.location_hint_for_user))
        } else {
            viewState.showProgressWheel()
            viewState.hideInfoMessage()
            onPhotoListLoad()
        }
    }

    init {
        App.appComponent!!.inject(this)
    }

    private fun onPhotoListLoad() {
        mLocationPhotosRepository.setRepositoryListener(this)
        mLocationPhotosRepository.startLoadPhotosListTask(mCurrentPage, mAuthUtils.getLocationPhotosUrl(mCurrentPage, AppConstants.NUM_OF_PHOTOS_ON_PAGE, latLng!!.latitude, latLng!!.longitude))
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
        mLocationPhotosRepository.unsubscribe()
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

    private fun loadData() {
        mLocationName = mPrefUtils.getLocationName()
        if (mLocationName == "") {
            mLocationName = App.appContext!!.getString(R.string.location_name)
        }
        val savedLatLng = mPrefUtils.getLocationLatLng()
        if (savedLatLng == null) {
            mIsFirstEntry = true
            mLocationName = LOCATION_CODE_NOT_SELECTED
        } else {
            mIsFirstEntry = false
            latLng = savedLatLng
            Logger.i("location presenter mLatLng = " + latLng!!.latitude + " ; " + latLng!!.longitude)
        }
    }

    fun onCoordinatesSelected(lat: Double, lng: Double, locationName: String) {
        mCurrentPage = 1
        mLocationName = locationName
        latLng = LatLng(lat, lng)
        mPrefUtils.putLocationName(mLocationName!!)
        Logger.i("location presenter saved locationName = $locationName")
        mPrefUtils.putLocationLatLng(latLng)
        if (mLocationName != null) {
            Logger.i("num of views = " + attachedViews.size)
            // todo fix bug
            // mapFragment opened -> screen rotated -> coordinates selected -> apply clicked -> getViewState == 0,
            // and after second screen rotation, new photos uploaded to locationFragment
            viewState.setLocationName(mLocationName!!)
        }
        onPhotoListLoad()
    }

    fun onMapCancelClicked() {
        val locationName = mPrefUtils.getLocationName()
        viewState.setLocationName(locationName)
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
