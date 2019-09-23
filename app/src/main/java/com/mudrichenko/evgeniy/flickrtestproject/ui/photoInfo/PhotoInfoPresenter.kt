package com.mudrichenko.evgeniy.flickrtestproject.ui.photoInfo

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.PhotoInfoRepository
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils
import com.orhanobut.logger.Logger

import javax.inject.Inject

@InjectViewState
class PhotoInfoPresenter : MvpPresenter<PhotoInfoView>(), PhotoInfoRepository.PhotoInfoRepositoryListener {

    @Inject
    lateinit var mPhotoInfoRepository: PhotoInfoRepository

    @Inject
    lateinit var mNetworkUtils: NetworkUtils

    init {
        App.appComponent!!.inject(this)
    }

    fun onPhotoInfoLoad(photoId: Long) {
        if (!mNetworkUtils.isInternetConnectionAvailable()) {
            viewState.showInfoMessage(App.appContext!!.resources.getString(R.string.no_internet_connection))
            return
        }
        viewState.hideInfoMessage()
        viewState.showProgressWheel()
        mPhotoInfoRepository.setPhotoInfoRepositoryListener(this)
        mPhotoInfoRepository.onPhotoInfoLoad(photoId)
    }

    fun onButtonDeleteClick(photoId: Long) {
        mPhotoInfoRepository.setPhotoInfoRepositoryListener(this)
        mPhotoInfoRepository.onPhotoDeleteFromServer(photoId)
    }

    override fun onErrorReceived(errorMessage: String) {
        viewState.hideProgressWheel()
        viewState.showInfoMessage(errorMessage)
    }

    override fun onPhotoDeleted() {
        Logger.i("PhotoInfoPresenter; onPhotoDeleted")
        viewState.onPhotoDeleted()
    }

    fun unsubscribe() {
        mPhotoInfoRepository.unsubscribe()
    }

    override fun onPhotoInfoReceived(flickrPhoto: FlickrPhoto) {
        viewState.showPhotoInfo(flickrPhoto)
        viewState.hideProgressWheel()
        viewState.hideInfoMessage()
    }

}
