package com.mudrichenko.evgeniy.flickrtestproject.data.repository

import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.FlickrPhotosTypes
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.api.ApiConstants
import com.mudrichenko.evgeniy.flickrtestproject.api.EndpointInterface
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.Photo
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotos.ResponsePhotos
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.ErrorUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.StringUtils
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@Suppress("MemberVisibilityCanBePrivate")
open class BasePhotosRepository {

    /***
     * Steps to get data:
     * 1) Error (no internet connection\server error etc): get data from local database
     *      1.1) No data in db/error: display "no internet connection" message
     *      1.2) Correct data: display data. Also notify user that we have problems getting remote data
     * 2) Correct remote data received:
     *      2.1) Save remote data in the device RAM
     *      2.2) Delete old data from database
     *          2.2.1) Error: go to [2.4]
     *          2.2.2) Success: go to [2.3]
     *      2.3) Upload new data into database
     *          2.3.1) Error: go to [2.4]
     *          2.3.2) Success: go to [2.4]
     *      2.4) Data received. Send data from the device RAM to listener
     */

    var mRepositoryListener: RepositoryListener? = null

    var disposables: ArrayList<Disposable>? = null

    open val FLICKR_PHOTO_TYPE: Int = FlickrPhotosTypes.TYPE_RECENT

    @Inject
    lateinit var mErrorUtils: ErrorUtils

    @Inject
    lateinit var mEndpointInterface: EndpointInterface

    @Inject
    lateinit var mAuthUtils: AuthUtils

    @Inject
    lateinit var mStringUtils: StringUtils

    @Inject
    lateinit var mNetworkUtils: NetworkUtils

    var mErrorCode: Int = ErrorUtils.ERROR_CODE_UNKNOWN

    var mErrorMessage: String? = null

    lateinit var mFlickrPhotos: List<FlickrPhoto>

    init {
        disposables = ArrayList()
    }

    fun startLoadPhotosListTask(page: Int, methodUrl: String) {
        unsubscribe()
        val disposable = mEndpointInterface.requestPhotosObservable(methodUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getRemotePhotosObserver(page))
        disposables?.add(disposable)
    }

    private fun getRemotePhotosObserver(page: Int): DisposableObserver<ResponsePhotos> {
        return object : DisposableObserver<ResponsePhotos>() {
            override fun onComplete() {
            }
            override fun onNext(response: ResponsePhotos) {
                if (response.stat == ApiConstants.RESPONSE_STAT_FAIL) {
                    mErrorCode = response.code
                    mErrorMessage = response.message
                    startLoadPhotosFromDb(page)
                    return
                }
                // check is last page
                if (page >= response.photos.pages) {
                    onLastPageReached()
                }
                // convert data
                mFlickrPhotos = convertServerPhotosToDbPhotos(page, response.photos.photo)
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

    fun convertServerPhotosToDbPhotos(page: Int, photos: List<Photo>): ArrayList<FlickrPhoto> {
        val flickrPhotos = ArrayList<FlickrPhoto>()
        for (x in photos.indices) {
            val photo = photos[x]
            val id = photo.id
            val imageUrl = mStringUtils.buildImageUrl(
                    id.toString(), photo.secret, photo.server, photo.farm)
            val flickrPhoto = FlickrPhoto(
                    0,
                    id,
                    FLICKR_PHOTO_TYPE,
                    page,
                    photo.title,
                    imageUrl,
                    photo.owner,
                    photo.ispublic == 1,
                    photo.isfamily == 1,
                    photo.isfriend == 1,
                    0,
                    null,
                    null,
                    null,
                    null,
                    0,
                    null,
                    null,
                    null,
                    null,
                    0,
                    0.0,
                    0.0,
                    0)
            flickrPhotos.add(flickrPhoto)
        }
        return flickrPhotos
    }

    fun startDeletePhotosFromDb() {
        val disposable = Completable.fromRunnable { App.database!!.flickrPhotoDao().removeAllPhotosByType(FLICKR_PHOTO_TYPE) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDeletePhotosFromDbObserver())
        disposables?.add(disposable)
    }

    fun getDeletePhotosFromDbObserver(): DisposableCompletableObserver {
        return object : DisposableCompletableObserver() {
            override fun onComplete() {
                startUploadPhotosToDb()
            }
            override fun onError(e: Throwable?) {
                onPhotoListUploadingFinished(mFlickrPhotos)
            }
        }
    }

    fun startUploadPhotosToDb() {
        val disposable = Completable.fromRunnable { App.database!!.flickrPhotoDao().insertPhotos(mFlickrPhotos) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getUploadPhotosToDbObserver())
        disposables?.add(disposable)
    }

    fun getUploadPhotosToDbObserver(): DisposableCompletableObserver {
        return object : DisposableCompletableObserver() {
            override fun onComplete() {
                onPhotoListUploadingFinished(mFlickrPhotos)
            }
            override fun onError(e: Throwable?) {
                onPhotoListUploadingFinished(mFlickrPhotos)
            }
        }
    }

    fun startLoadPhotosFromDb(page: Int) {
        val disposable = App.database?.flickrPhotoDao()!!.getPhotosByTypeAndPage(FLICKR_PHOTO_TYPE, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getLoadPhotosFromDbObserver())
        disposables?.add(disposable)
    }

    fun getLoadPhotosFromDbObserver(): DisposableSingleObserver<List<FlickrPhoto>> {
        return object : DisposableSingleObserver<List<FlickrPhoto>>() {
            override fun onSuccess(flickrPhotos: List<FlickrPhoto>) {
                if (!mNetworkUtils.isInternetConnectionAvailable()) {
                    onInternetConnectionProblemNotify()
                }
                if (flickrPhotos.isEmpty()) {
                    // we have no data
                    if (!mNetworkUtils.isInternetConnectionAvailable()) {
                        onErrorReceived(App.appContext!!.getString(R.string.no_internet_connection))
                    } else {
                        onErrorReceived(mErrorUtils.getErrorMessage(mErrorCode, mErrorMessage))
                    }
                } else {
                    onPhotoListUploadingFinished(flickrPhotos)
                }
            }
            override fun onError(e: Throwable?) {
                onErrorReceived(mErrorUtils.getErrorMessage(mErrorCode, mErrorMessage))
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

    fun onErrorReceived(errorMessage: String) {
        if (mRepositoryListener != null) {
            mRepositoryListener?.onErrorReceived(errorMessage)
        }
    }

    fun onInternetConnectionProblemNotify() {
        if (mRepositoryListener != null) {
            mRepositoryListener?.onInternetConnectionProblemNotify()
        }
    }

    fun onLastPageReached() {
        if (mRepositoryListener != null) {
            mRepositoryListener?.onLastPageReached()
        }
    }

    fun onPhotoListUploadingFinished(flickrPhotos: List<FlickrPhoto>) {
        if (mRepositoryListener != null) {
            mRepositoryListener?.onPhotoListUploadingFinishedListener(flickrPhotos)
        }
    }

    interface RepositoryListener {
        fun onErrorReceived(errorMessage: String)
        fun onInternetConnectionProblemNotify()
        fun onPhotoListUploadingFinishedListener(flickrPhotoList: List<FlickrPhoto>)
        fun onLastPageReached()
    }

    fun setRepositoryListener(repositoryListener: RepositoryListener) {
        mRepositoryListener = repositoryListener
    }

}