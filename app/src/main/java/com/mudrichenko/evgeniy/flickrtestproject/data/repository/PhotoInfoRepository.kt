package com.mudrichenko.evgeniy.flickrtestproject.data.repository

import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.api.ApiConstants
import com.mudrichenko.evgeniy.flickrtestproject.api.EndpointInterface
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responseDeletePhoto.ResponseDeletePhoto
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo.Photo
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo.ResponsePhotoInfo
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.ErrorUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PhotoInfoRepository {

    private var mPhotoInfoRepositoryListener: PhotoInfoRepositoryListener? = null

    @Inject
    lateinit var mEndpointInterface: EndpointInterface

    @Inject
    lateinit var mErrorUtils: ErrorUtils

    @Inject
    lateinit var mAuthUtils: AuthUtils

    private var disposables: ArrayList<Disposable>? = null

    @Inject
    lateinit var mNetworkUtils: NetworkUtils

    private var mPhotoId: Long = 0

    private var mErrorCode: Int = 0

    private var mErrorMessage: String? = ""

    init {
        App.appComponent!!.inject(this)
        disposables = ArrayList()
    }

    fun onPhotoInfoLoad(photoId: Long) {
        mPhotoId = photoId
        unsubscribe()
        val disposable = mEndpointInterface.requestPhotoInfo(mAuthUtils.getPhotoInfoUrl(photoId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getPhotoInfoObserver())
        disposables?.add(disposable)
    }

    private fun getPhotoInfoObserver(): DisposableObserver<ResponsePhotoInfo> {
        return object : DisposableObserver<ResponsePhotoInfo>() {
            override fun onComplete() {

            }
            override fun onNext(response: ResponsePhotoInfo) {
                if (response.stat != null) {
                    if (response.stat == ApiConstants.RESPONSE_STAT_FAIL) {
                        mErrorCode = response.code
                        mErrorMessage = response.message
                        val photo = Photo()
                        photo.id = mPhotoId
                        startLoadPhotoInfoFromDb(photo, false)
                        return
                    }
                }
                if (response.photo == null) {
                    val photo = Photo()
                    photo.id = mPhotoId
                    startLoadPhotoInfoFromDb(photo, false)
                } else {
                    startUpdatePhotoInfoInDb(response.photo!!)
                }
            }
            override fun onError(e: Throwable) {
                mErrorCode = ErrorUtils.ERROR_CODE_UNKNOWN
                val photo = Photo()
                photo.id = mPhotoId
                startLoadPhotoInfoFromDb(photo, false)
            }
        }
    }

    private fun startUpdatePhotoInfoInDb(photo: Photo) {
        startLoadPhotoInfoFromDb(photo, true)
    }

    private fun startUploadPhotoInfoToDb(flickrPhoto: FlickrPhoto) {
        val disposable = Completable.fromRunnable { App.database!!.flickrPhotoDao().insertPhoto(flickrPhoto) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getUploadPhotoInfoToDbObserver(flickrPhoto))
        disposables?.add(disposable)
    }

    private fun getUploadPhotoInfoToDbObserver(flickrPhoto: FlickrPhoto): DisposableCompletableObserver {
        return object : DisposableCompletableObserver() {
            override fun onComplete() {
                onPhotoInfoReceived(flickrPhoto)
            }
            override fun onError(e: Throwable) {
                onPhotoInfoReceived(flickrPhoto)
            }
        }
    }

    private fun startLoadPhotoInfoFromDb(photo: Photo, isNeedToDeletePhoto: Boolean) {
        val disposable = App.database?.flickrPhotoDao()!!.getByFlickrId(photo.id!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getLoadPhotoInfoFromDbObserver(photo, isNeedToDeletePhoto))
        disposables?.add(disposable)
    }

    private fun getLoadPhotoInfoFromDbObserver(photo: Photo, isNeedToDeletePhoto: Boolean): DisposableSingleObserver<FlickrPhoto> {
        return object : DisposableSingleObserver <FlickrPhoto>() {
            override fun onSuccess(flickrPhoto: FlickrPhoto) {
                if (flickrPhoto == null) {
                    if (!mNetworkUtils.isInternetConnectionAvailable()) {
                        onErrorReceived(App.appContext!!.getString(R.string.no_internet_connection))
                    } else {
                        onErrorReceived(mErrorUtils.getErrorMessage(mErrorCode, mErrorMessage))
                    }
                } else {
                    if (isNeedToDeletePhoto) {
                        combineData(photo, flickrPhoto)
                    } else {
                        onPhotoInfoReceived(flickrPhoto)
                    }
                }
            }
            override fun onError(e: Throwable) {
                onErrorReceived(mErrorUtils.getErrorMessage(mErrorCode, mErrorMessage))
            }
        }
    }

    private fun combineData(photo: Photo, flickrPhoto: FlickrPhoto) {
        flickrPhoto.dateTaken = photo.dates?.taken
        flickrPhoto.description = photo.description?.content
        flickrPhoto.isFamily = (photo.visibility?.isFamily == 1)
        flickrPhoto.isFriend = (photo.visibility?.isFriend == 1)
        flickrPhoto.isPublic = (photo.visibility?.isPublic == 1)
        //flickrPhoto.locationAccuracy = photo.location!!.accuracy!!
        //flickrPhoto.locationLat = photo.location!!.latitude!!
        //flickrPhoto.locationLng = photo.location!!.longitude!!
        flickrPhoto.name = photo.title!!.content!!
        flickrPhoto.numOfViews = photo.views!!
        if (photo.owner?.nsid != null) {
            flickrPhoto.ownerId = photo.owner?.nsid!!
        }
        flickrPhoto.ownerName = photo.owner?.userName
        flickrPhoto.ownerRealName = photo.owner?.realName
        //flickrPhoto.safetyLevel = photo.safetyLevel
        //flickrPhoto.tagIds = photo.tags!!.tag!!
        //flickrPhoto.tagRaw = photo.tags
        startUploadPhotoInfoToDb(flickrPhoto)
    }

    fun onPhotoDeleteFromServer(photoId: Long) {
        unsubscribe()
        val disposable = mEndpointInterface.requestDeletePhoto(mAuthUtils.getDeletePhotoUrl(photoId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getPhotoDeleteFromServerObserver())
        disposables?.add(disposable)
    }

    private fun getPhotoDeleteFromServerObserver(): DisposableObserver<ResponseDeletePhoto> {
        return object : DisposableObserver<ResponseDeletePhoto>() {
            override fun onComplete() {

            }
            override fun onNext(response: ResponseDeletePhoto) {
                if (response.stat != null) {
                    if (response.stat == ApiConstants.RESPONSE_STAT_FAIL) {
                        onErrorReceived(mErrorUtils.getErrorMessage(response.code, response.message))
                    } else {
                        if (mPhotoInfoRepositoryListener != null) {
                            mPhotoInfoRepositoryListener!!.onPhotoDeleted()
                        }
                    }
                } else {
                    onErrorReceived(mErrorUtils.getErrorMessage(-1, null))
                }
            }
            override fun onError(e: Throwable) {
                onErrorReceived(mErrorUtils.getErrorMessage(-1, null))
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

    private fun onPhotoInfoReceived(flickrPhoto: FlickrPhoto) {
        if (mPhotoInfoRepositoryListener != null) {
            mPhotoInfoRepositoryListener!!.onPhotoInfoReceived(flickrPhoto)
        }
    }

    private fun onErrorReceived(errorMessage: String) {
        if (mPhotoInfoRepositoryListener != null) {
            mPhotoInfoRepositoryListener!!.onErrorReceived(errorMessage)
        }
    }

    interface PhotoInfoRepositoryListener {
        fun onPhotoInfoReceived(flickrPhoto: FlickrPhoto)
        fun onErrorReceived(errorMessage: String)
        fun onPhotoDeleted()
    }

    fun setPhotoInfoRepositoryListener(photoInfoRepositoryListener: PhotoInfoRepositoryListener) {
        mPhotoInfoRepositoryListener = photoInfoRepositoryListener
    }

}