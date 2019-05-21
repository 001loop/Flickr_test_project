package com.mudrichenko.evgeniy.flickrtestproject.data.repository

import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.AppConstants
import com.mudrichenko.evgeniy.flickrtestproject.FlickrPhotosTypes
import com.mudrichenko.evgeniy.flickrtestproject.api.ApiConstants
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoset.ResponsePhotoset
import com.mudrichenko.evgeniy.flickrtestproject.utils.ErrorUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class AlbumsPhotosRepository: BasePhotosRepository() {

    override val FLICKR_PHOTO_TYPE = FlickrPhotosTypes.TYPE_ALBUMS

    init {
        App.appComponent!!.inject(this)
        disposables = ArrayList()
    }

    fun startLoadPhotosListTask(page: Int, userId: String, photosetId: Long) {
        unsubscribe()
        val disposable = mEndpointInterface.requestPhotoset(mAuthUtils.getAlbumPhotosUrl(page, AppConstants.NUM_OF_PHOTOS_ON_PAGE, userId, photosetId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getRemotePhotosObserver(page))
        disposables?.add(disposable)
    }

    private fun getRemotePhotosObserver(page: Int): DisposableObserver<ResponsePhotoset> {
        return object : DisposableObserver<ResponsePhotoset>() {
            override fun onComplete() {
            }
            override fun onNext(response: ResponsePhotoset) {
                if (response.stat == ApiConstants.RESPONSE_STAT_FAIL) {
                    mErrorCode = response.code
                    mErrorMessage = response.message
                    startLoadPhotosFromDb(page)
                    return
                }
                // check is last page
                if (page >= response.photoset.pages) {
                    onLastPageReached()
                }
                // convert data
                mFlickrPhotos = convertServerPhotosToDbPhotos(page, response.photoset.photo)
                // data is correct. Send data into DB
                if (page == 1) {
                    startDeletePhotosFromDb()
                } else {
                    startUploadPhotosToDb()
                }
            }
            override fun onError(e: Throwable?) {
                mErrorCode = ErrorUtils.ERROR_CODE_INTERNET
                startLoadPhotosFromDb(page)
            }
        }
    }

}