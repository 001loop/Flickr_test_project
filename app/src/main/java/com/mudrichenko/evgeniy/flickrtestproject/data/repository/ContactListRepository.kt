package com.mudrichenko.evgeniy.flickrtestproject.data.repository

import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.AppConstants
import com.mudrichenko.evgeniy.flickrtestproject.api.ApiConstants
import com.mudrichenko.evgeniy.flickrtestproject.api.EndpointInterface
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responseContactList.Contact
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responseContactList.ResponseContactList
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrContact
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils
import com.mudrichenko.evgeniy.flickrtestproject.utils.ErrorUtils
import com.orhanobut.logger.Logger
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

class ContactListRepository {

    private var mContactListRepositoryListener: ContactListRepositoryListener? = null

    private var disposables: ArrayList<Disposable>? = null

    @Inject
    lateinit var mAuthUtils: AuthUtils

    @Inject
    lateinit var mErrorUtils: ErrorUtils

    var mErrorCode: Int = 0

    var mErrorMessage: String? = ""

    @Inject
    lateinit var mEndpointInterface: EndpointInterface

    var mCurrentNumOfInvalidSignatureErrorRetry = 0

    init {
        App.appComponent!!.inject(this)
        disposables = ArrayList()
        mCurrentNumOfInvalidSignatureErrorRetry = 0
    }

    fun startLoadContactListTask() {
        unsubscribe()
        val disposable = mEndpointInterface.requestContactList(mAuthUtils.getContactListUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getLoadContactListObserver())
        disposables?.add(disposable)
    }


    /***
    1) get contact-list from server
     1.1) error (no internet connection, server error, wrong data): get contact-list from db
     1.2) correct data received: delete old contacts from db, write new contacts, send new contacts to presenter

     2) get contact-list from db:
     2.1) error: display error with message and code
     2.2) all ok, send data to presenter
    */

    private fun getLoadContactListObserver(): DisposableObserver<ResponseContactList> {
        return object : DisposableObserver<ResponseContactList>() {
            override fun onComplete() {
            }
            override fun onNext(response: ResponseContactList) {
                if (response.stat.equals("ok")) {
                    startDeleteContactsFromDb(response.contacts?.contact)
                } else {
                    onErrorReceived(mErrorUtils.getErrorMessage(-1, null))
                }
            }
            override fun onError(e: Throwable?) {
                if (e !is HttpException) {
                    // unknown error (probably no internet connection)
                    mErrorCode = ErrorUtils.ERROR_CODE_UNKNOWN
                    onErrorReceived(mErrorUtils.getErrorMessage(-1, null))
                    unsubscribe()
                    return
                }
                val errorBody = e.response().errorBody()!!.string()
                if (errorBody.contains(ApiConstants.RESPONSE_ERROR_INVALID_SIGNATURE)) {
                    mCurrentNumOfInvalidSignatureErrorRetry ++
                    if (mCurrentNumOfInvalidSignatureErrorRetry < AppConstants.API_INVALID_SIGNATURE_MAX_NUM_OF_RETRY) {
                        unsubscribe()
                        startLoadContactListTask()
                    }
                }
                onErrorReceived(mErrorUtils.getErrorMessage(-1, null))
            }
        }
    }

    private fun startDeleteContactsFromDb(contacts: List<Contact>?) {
        val disposable = Completable.fromRunnable { App.database!!.flickrContactDao().removeAll() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDeleteContactListObserver(contacts))
        disposables?.add(disposable)
    }

    private fun getDeleteContactListObserver(contacts: List<Contact>?): DisposableCompletableObserver {
        return object : DisposableCompletableObserver() {
            override fun onComplete() {
                Logger.i("getDeleteContactListObserver; contacts size = " + contacts!!.size)
                startUploadContactsToDb(contacts)
            }
            override fun onError(e: Throwable?) {
                Logger.i("getDeleteContactListObserver; onError; e = " + e.toString())
                startLoadContactsFromDb()
            }
        }
    }

    private fun startUploadContactsToDb(contacts: List<Contact>?) {
        if (contacts == null) {
            return
        }
        Logger.i("startUploadContactsToDb; contacts size = " + contacts.size)
        val flickrContacts = ArrayList<FlickrContact>()
        for (x in contacts.indices) {
            val contact = contacts[x]
            val flickrContact = FlickrContact(
                    contact.nsid ?: "",
                    contact.username ?: "",
                    contact.realName ?: "",
                    contact.family == 1,
                    contact.friend == 1,
                    contact.ignored == 1)
            flickrContacts.add(flickrContact)
        }
        Logger.i("uploadtodb; flickrContacts size = " + flickrContacts.size)
        val disposable = Completable.fromRunnable { App.database!!.flickrContactDao().insert(flickrContacts) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getUploadContactsToDbObserver())
        disposables?.add(disposable)
    }

    private fun getUploadContactsToDbObserver(): DisposableCompletableObserver {
        return object : DisposableCompletableObserver() {
            override fun onComplete() {
                startLoadContactsFromDb()
            }
            override fun onError(e: Throwable?) {
                startLoadContactsFromDb()
            }
        }
    }

    private fun startLoadContactsFromDb() {
        val disposable = App.database!!.flickrContactDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getLoadContactsFromDbObserver())
        disposables?.add(disposable)
    }

    private fun getLoadContactsFromDbObserver(): DisposableSingleObserver<List<FlickrContact>> {
        return object : DisposableSingleObserver<List<FlickrContact>>() {
            override fun onSuccess(contacts: List<FlickrContact>?) {
                if (contacts != null) {
                    Logger.i("contactsReceivedDB; size = " + contacts.size)
                    onContactListReceived(contacts)
                } else {
                    Logger.i("contactsReceivedDB null")
                    onErrorReceived(mErrorUtils.getErrorMessage(-1, null))
                }
            }
            override fun onError(e: Throwable?) {
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

    private fun onContactListReceived(contacts: List<FlickrContact>) {
        if (mContactListRepositoryListener != null) {
            mContactListRepositoryListener!!.onContactListReceived(contacts)
        }
    }

    private fun onErrorReceived(message: String) {
        if (mContactListRepositoryListener != null) {
            mContactListRepositoryListener!!.onErrorReceived(message)
        }
    }

    interface ContactListRepositoryListener {
        fun onContactListReceived(contactList: List<FlickrContact>)
        fun onErrorReceived(errorMessage: String)
    }

    fun setContactListRepositoryListener(contactListRepositoryListener: ContactListRepositoryListener) {
        mContactListRepositoryListener = contactListRepositoryListener
    }

}