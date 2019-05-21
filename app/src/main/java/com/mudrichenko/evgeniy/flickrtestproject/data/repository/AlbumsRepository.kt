package com.mudrichenko.evgeniy.flickrtestproject.data.repository

import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.api.EndpointInterface
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotosets.Photoset
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotosets.ResponsePhotosets
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhotoset
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.ErrorUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils
import com.orhanobut.logger.Logger
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
import javax.inject.Inject

class AlbumsRepository {

    private var mAlbumsRepositoryListener: AlbumsRepositoryListener? = null

    private var disposables: ArrayList<Disposable>? = null

    @Inject
    lateinit var mAuthUtils: AuthUtils

    @Inject
    lateinit var mPrefUtils: PrefUtils

    @Inject
    lateinit var mErrorUtils: ErrorUtils

    @Inject
    lateinit var mEndpointInterface: EndpointInterface

    var mCurrentNumOfInvalidSignatureErrorRetry = 0

    init {
        App.appComponent!!.inject(this)
        disposables = ArrayList()
        mCurrentNumOfInvalidSignatureErrorRetry = 0
    }

    fun startLoadPhotosetsListTask() {
        Logger.i("AlbumsRepository; startLoadPhotosetsListTask")
        unsubscribe()
        val disposable = mEndpointInterface.requestPhotosets(mAuthUtils.getPhotosetsUrl(mPrefUtils.getUserId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getRemotePhotosetsObserver())
        disposables!!.add(disposable)
    }

    private fun getRemotePhotosetsObserver(): DisposableObserver<ResponsePhotosets> {
        return object : DisposableObserver<ResponsePhotosets>() {
            override fun onNext(response: ResponsePhotosets) {
                if (response.stat.equals("ok")) {
                    Logger.i("AlbumsRepository; startLoadPhotosetsListTask ok")
                    startDeletePhotosetsFromDb(response.photosets!!.photoset)
                } else {
                    startLoadPhotosetsFromDb()
                }
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                startLoadPhotosetsFromDb()
            }

            override fun onComplete() {

            }
        }
    }
    //--------------------------------------------------------------------//

    //--------------------------------------------------------------------//
    //                          Delete photosets from DB
    private fun startDeletePhotosetsFromDb(photosets: List<Photoset>?) {
        Logger.i("AlbumsRepository; startDeletePhotosetsFromDb")
        val disposable = Completable.fromRunnable { App.database!!.flickrPhotosetDao().removeAll() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        startUploadPhotosetsToDb(photosets!!)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        startLoadPhotosetsFromDb()
                    }
                })
        disposables!!.add(disposable)
    }
    //--------------------------------------------------------------------//

    //--------------------------------------------------------------------//
    //                          Upload photosets to DB
    private fun startUploadPhotosetsToDb(photosets: List<Photoset>) {
        Logger.i("AlbumsRepository; startUploadPhotosetsToDb")
        val flickrPhotosets = ArrayList<FlickrPhotoset>()
        for (x in photosets.indices) {
            val currentPhotoset = photosets[x]
            val id = currentPhotoset.id!!
            val photos = currentPhotoset.photos!!
            val videos = currentPhotoset.videos!!
            val countViews = currentPhotoset.countViews!!
            val dateCreate = currentPhotoset.dateCreate!!
            val dateUpdate = currentPhotoset.dateUpdate!!
            val title = currentPhotoset.title!!.content
            val description = currentPhotoset.description!!.content
            // add photoset to db
            flickrPhotosets.add(FlickrPhotoset(id, mPrefUtils.getUserId(),
                    photos, videos, countViews, dateCreate, dateUpdate,
                    title!!, description!!))
        }
        val disposable = Completable.fromRunnable { App.database!!.flickrPhotosetDao().insert(flickrPhotosets) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        startLoadPhotosetsFromDb()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        startLoadPhotosetsFromDb()
                    }
                })
        disposables!!.add(disposable)
    }

    private fun startLoadPhotosetsFromDb() {
        Logger.i("AlbumsRepository; startLoadPhotosetsFromDb")
        val disposable = App.database!!.flickrPhotosetDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<FlickrPhotoset>>() {
                    override fun onSuccess(photosets: List<FlickrPhotoset>) {
                        onAlbumsReceived(photosets)
                    }

                    override fun onError(e: Throwable) {
                        onErrorReceived(mErrorUtils.getErrorMessage(-1, null))
                    }
                })
        disposables!!.add(disposable)
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

    private fun onAlbumsReceived(albums: List<FlickrPhotoset>) {
        if (mAlbumsRepositoryListener != null) {
            mAlbumsRepositoryListener!!.onAlbumsListReceived(albums)
        }
    }

    private fun onErrorReceived(message: String) {
        if (mAlbumsRepositoryListener != null) {
            mAlbumsRepositoryListener!!.onErrorReceived(message)
        }
    }

    interface AlbumsRepositoryListener {
        fun onAlbumsListReceived(albumsList: List<FlickrPhotoset>)
        fun onErrorReceived(errorMessage: String)
    }

    fun setAlbumsRepositoryListener(albumsRepositoryListener: AlbumsRepositoryListener) {
        mAlbumsRepositoryListener = albumsRepositoryListener
    }

    /*
    private var mAlbumsRepositoryListener: AlbumsRepositoryListener? = null

    @Inject
    lateinit var mEndpointInterface: EndpointInterface

    @Inject
    lateinit var mStringUtils: StringUtils

    @Inject
    lateinit var mAuthUtils: AuthUtils

    @Inject
    lateinit var mPrefUtils: PrefUtils

    @Inject
    lateinit var mErrorUtils: ErrorUtils

    private val FLICKR_PHOTO_TYPE = FlickrPhotosTypes.TYPE_ALBUMS

    private var disposables: ArrayList<Disposable>? = null

    init {
        App.appComponent!!.inject(this)
        disposables = ArrayList()
    }

    //--------------------------------------------------------------------//
    //                      Load photos from server
    fun startLoadPhotosListTask(page: Int, userId: String, photosetId: Long) {
        unsubscribe()
        val disposable = mEndpointInterface.requestPhotoset(mAuthUtils.getAlbumPhotosUrl(page, AppConstants.NUM_OF_PHOTOS_ON_PAGE, userId, photosetId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getRemotePhotosObserver(page, photosetId))
        disposables?.add(disposable)
    }

    private fun getRemotePhotosObserver(page: Int, photosetId: Long): DisposableObserver<ResponsePhotoset> {
        return object : DisposableObserver<ResponsePhotoset>() {
            override fun onNext(response: ResponsePhotoset) {
                if (response.stat.equals("ok")) {
                    if (page == 1) {
                        // we received new photos. Remove old photos and then upload new into DB
                        startDeletePhotosFromDb(response.photoset!!.photo, page, photosetId)
                    } else {
                        startUploadPhotosToDb(response.photoset!!.photo!!, page, photosetId)
                    }
                } else {
                    startLoadPhotosFromDb(page, photosetId)
                }
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                startLoadPhotosFromDb(page, photosetId)
            }

            override fun onComplete() {

            }
        }
    }
    //--------------------------------------------------------------------//

    //--------------------------------------------------------------------//
    //                          Delete photos from DB
    private fun startDeletePhotosFromDb(photos: List<Photo>?, page: Int, photosetId: Long) {
        val disposable = Completable.fromRunnable { App.database!!.flickrPhotoDao().removeAllPhotosByTypeAndAlbum(FLICKR_PHOTO_TYPE, photosetId) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableCompletableObserver(photos, page, photosetId))
        disposables!!.add(disposable)
    }

    private fun getDisposableCompletableObserver(photos: List<Photo>?, page: Int, photosetId: Long): DisposableCompletableObserver {
        return object : DisposableCompletableObserver() {
            override fun onComplete() {
                startUploadPhotosToDb(photos!!, page, photosetId)
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                startLoadPhotosFromDb(page, photosetId)
            }
        }
    }
    //--------------------------------------------------------------------//

    //--------------------------------------------------------------------//
    //                          Upload photos to DB
    private fun startUploadPhotosToDb(photos: List<Photo>, page: Int, photosetId: Long) {
        // todo get sizes
        val flickrPhotos = ArrayList<FlickrPhoto>()
        for (x in photos.indices) {
            val id = photos[x].id!!
            val imageUrl = mStringUtils.buildImageUrl(
                    id.toString(),
                    photos[x].secret!!,
                    photos[x].server!!,
                    photos[x].farm!!)
            flickrPhotos.add(
                    FlickrPhoto(
                            0,
                            id,
                            FLICKR_PHOTO_TYPE,
                            page,
                            photos[x].title ?: "",
                            imageUrl,
                            photos[x].owner ?: "",
                            photos[x].isPublic == 1,
                            photos[x].isFamily == 1,
                            photos[x].isFriend == 1,
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
            )
        }

        val disposable = Completable.fromRunnable { App.database!!.flickrPhotoDao().insertPhotos(flickrPhotos) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        startLoadPhotosFromDb(page, photosetId)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        startLoadPhotosFromDb(page, photosetId)
                    }
                })
        disposables!!.add(disposable)
    }
    //--------------------------------------------------------------------//

    //--------------------------------------------------------------------//
    //                          Load photos from DB
    private fun startLoadPhotosFromDb(currentPage: Int, photosetId: Long) {
        val disposable = App.database!!.flickrPhotoDao().getPhotosByAlbumAndPage(FLICKR_PHOTO_TYPE, currentPage, photosetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<FlickrPhoto>>() {
                    override fun onSuccess(flickrPhotos: List<FlickrPhoto>) {
                        if (mAlbumsRepositoryListener != null) {
                            mAlbumsRepositoryListener!!.onPhotoListUploadingFinishedListener(flickrPhotos)
                        }
                    }

                    override fun onError(e: Throwable) {
                        onErrorReceived(mErrorUtils.getErrorMessage(-1, null))
                    }
                })
        disposables!!.add(disposable)
    }
    //--------------------------------------------------------------------//

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

    private fun onErrorReceived(message: String) {
        if (mAlbumsRepositoryListener != null) {
            mAlbumsRepositoryListener?.onErrorReceived(message)
        }
    }

    interface AlbumsRepositoryListener {

        fun onPhotosetListUploadingFinishedListener(flickrPhotosetList: List<FlickrPhotoset>)

        fun onPhotoListUploadingFinishedListener(flickrPhotoList: List<FlickrPhoto>)

        fun onErrorReceived(errorMessage: String)

    }

    fun setAlbumsRepositoryListener(albumsRepositoryListener: AlbumsRepositoryListener) {
        mAlbumsRepositoryListener = albumsRepositoryListener
    }
*/
}