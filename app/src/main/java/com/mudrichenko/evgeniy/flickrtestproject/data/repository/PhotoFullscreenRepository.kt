package com.mudrichenko.evgeniy.flickrtestproject.data.repository

import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.api.ApiConstants
import com.mudrichenko.evgeniy.flickrtestproject.api.EndpointInterface
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoSizes.ResponsePhotoSizes
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.ErrorUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.StringUtils
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PhotoFullscreenRepository {

    private var mPhotoFullscreenRepositoryListener: PhotoFullscreenRepositoryListener? = null

    private var disposables: ArrayList<Disposable>? = null

    @Inject
    lateinit var mErrorUtils: ErrorUtils

    @Inject
    lateinit var mEndpointInterface: EndpointInterface

    @Inject
    lateinit var mAuthUtils: AuthUtils

    @Inject
    lateinit var mStringUtils: StringUtils

    init {
        App.appComponent!!.inject(this)
        disposables = ArrayList()
    }

    fun startLoadImageSizesTask(photo: FlickrPhoto) {
        val disposable = mEndpointInterface.requestPhotoSizes(mAuthUtils.getPhotoSizesUrl(photo.flickrId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getImageSizesObserver(photo))
        disposables?.add(disposable)
    }

    private fun getImageSizesObserver(photo: FlickrPhoto): DisposableObserver<ResponsePhotoSizes> {
        return object : DisposableObserver<ResponsePhotoSizes>() {
            override fun onComplete() {
                // nothing
            }
            override fun onNext(response: ResponsePhotoSizes) {
                if (response.stat != null) {
                    if (response.stat == ApiConstants.RESPONSE_STAT_FAIL) {
                        onErrorReceived()
                        return
                    }
                }
                if (response.sizes == null) {
                    onErrorReceived()
                    return
                }
                for (size in response.sizes!!.size!!) {
                    if (size.label.equals(ApiConstants.PHOTO_SIZE_LABEL_ORIGINAL_SIZE) && size.source != null) {
                        photo.urlOriginalSize = size.source!!
                        break
                    }
                }
                if (photo.urlOriginalSize == null) {
                    photo.urlOriginalSize = response.sizes!!.size!![response.sizes!!.size!!.size - 1].source
                }
                val disposable = Completable.fromRunnable { App.database!!.flickrPhotoDao().insertPhoto(photo) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(getUploadPhotoToDbObserver())
                disposables?.add(disposable)
                onPhotoUrlReceived(photo)
            }
            override fun onError(e: Throwable) {
                // nothing
                onErrorReceived()
            }
        }
    }

    private fun getUploadPhotoToDbObserver(): DisposableCompletableObserver {
        return object : DisposableCompletableObserver() {
            override fun onComplete() {
                // nothing
            }
            override fun onError(e: Throwable) {
                // nothing
            }
        }
    }

    fun unsubscribe() {
        if (disposables == null) {
            return
        }
        for (x in disposables!!.indices) {
            val disposable = disposables!![x]
            if (!disposable.isDisposed) {
                disposable.dispose()
            }
        }
        disposables?.clear()
    }

    private fun onPhotoUrlReceived(photo: FlickrPhoto) {
        if (mPhotoFullscreenRepositoryListener != null) {
            mPhotoFullscreenRepositoryListener?.onPhotoSizesUploadingFinishedListener(photo)
        }
    }

    private fun onErrorReceived() {
        if (mPhotoFullscreenRepositoryListener != null) {
            mPhotoFullscreenRepositoryListener?.onErrorReceived()
        }
    }

    interface PhotoFullscreenRepositoryListener {
        fun onErrorReceived()
        fun onPhotoSizesUploadingFinishedListener(flickrPhoto: FlickrPhoto)
    }

    fun setPhotoFullscreenRepositoryListener(photoFullscreenRepositoryListener: PhotoFullscreenRepositoryListener) {
        mPhotoFullscreenRepositoryListener = photoFullscreenRepositoryListener
    }

}