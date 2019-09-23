package com.mudrichenko.evgeniy.flickrtestproject.ui.photoFullscreen

import android.os.Bundle

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.PhotoFullscreenRepository
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils

import javax.inject.Inject

@InjectViewState
class PhotoFullscreenPresenter : MvpPresenter<PhotoFullscreenView>(), PhotoFullscreenRepository.PhotoFullscreenRepositoryListener {

    @Inject
    lateinit var mNetworkUtils: NetworkUtils

    @Inject
    lateinit var mPhotoFullscreenRepository: PhotoFullscreenRepository

    private var mFlickrPhoto: FlickrPhoto? = null

    init {
        App.appComponent!!.inject(this)
    }

    fun onPhotoLoad(bundle: Bundle?) {
        viewState.startLoadingPhoto()
        if (bundle != null) {
            mFlickrPhoto = bundle.getSerializable("flickrPhoto") as FlickrPhoto
            if (mFlickrPhoto != null) {
                if (mFlickrPhoto!!.urlOriginalSize != null) {
                    viewState.showPhoto(mFlickrPhoto!!, mFlickrPhoto!!.urlOriginalSize!!)
                } else {
                    if (mNetworkUtils.isInternetConnectionAvailable()) {
                        mPhotoFullscreenRepository.setPhotoFullscreenRepositoryListener(this)
                        mPhotoFullscreenRepository.startLoadImageSizesTask(mFlickrPhoto!!)
                    } else {
                        viewState.showPhoto(mFlickrPhoto!!, mFlickrPhoto!!.url)
                    }
                }
            }
        }
    }

    fun unsubscribe() {
        mPhotoFullscreenRepository.unsubscribe()
    }

    override fun onErrorReceived() {
        // nothing to do with error, just show low-resolution image
        viewState.showPhoto(mFlickrPhoto!!, mFlickrPhoto!!.url)
    }

    override fun onPhotoSizesUploadingFinishedListener(flickrPhoto: FlickrPhoto) {
        mFlickrPhoto = flickrPhoto
        if (mFlickrPhoto!!.urlOriginalSize != null) {
            viewState.showPhoto(mFlickrPhoto!!, mFlickrPhoto!!.urlOriginalSize!!)
        } else {
            viewState.showPhoto(mFlickrPhoto!!, mFlickrPhoto!!.url)
        }
    }

}
