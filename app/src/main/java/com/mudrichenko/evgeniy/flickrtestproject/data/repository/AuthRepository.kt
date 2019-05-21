package com.mudrichenko.evgeniy.flickrtestproject.data.repository

import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.api.EndpointInterface
import com.mudrichenko.evgeniy.flickrtestproject.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import javax.inject.Inject

class AuthRepository {

    private var mAuthRepositoryListener: AuthRepositoryListener? = null

    @Inject
    lateinit var mEndpointInterface: EndpointInterface

    @Inject
    lateinit var mNetworkUtils: NetworkUtils

    @Inject
    lateinit var mAuthUtils: AuthUtils

    @Inject
    lateinit var mStringUtils: StringUtils

    @Inject
    lateinit var mPrefUtils: PrefUtils

    @Inject
    lateinit var mErrorUtils: ErrorUtils

    private var disposables: ArrayList<Disposable>? = null

    init {
        App.appComponent!!.inject(this)
        disposables = ArrayList()
    }

    fun onRequestToken(authRepositoryListener: AuthRepositoryListener) {
        mAuthRepositoryListener = authRepositoryListener
        unsubscribe()
        val disposable = mEndpointInterface.simpleRequest(mAuthUtils.getRequestTokenUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getRequestTokenObserver())
        disposables?.add(disposable)
    }

    private fun getRequestTokenObserver(): DisposableObserver<ResponseBody> {
        return object : DisposableObserver<ResponseBody>() {
            override fun onComplete() {

            }

            override fun onNext(responseBody: ResponseBody?) {
                val responseBodyString = responseBody?.string()
                if (responseBody != null) {
                    if (mAuthRepositoryListener != null) {
                        mAuthRepositoryListener!!.onAuthTokenReceived(responseBodyString!!)
                    }
                } else {
                    onErrorReceived(mErrorUtils.getErrorMessage(-1, null))
                }
            }

            override fun onError(e: Throwable?) {
                onErrorReceived(mErrorUtils.getErrorMessage(-1, null))
            }
        }
    }

    fun onRequestAccessToken(authRepositoryListener: AuthRepositoryListener, verifier: String?, token: String?) {
        if (verifier == null || token == null) {
            return
        }
        mAuthRepositoryListener = authRepositoryListener
        unsubscribe()
        val disposable = mEndpointInterface.simpleRequest(mAuthUtils.getAccessTokenUrl(token, verifier))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getAccessTokenUrlObserver())
        disposables!!.add(disposable)
    }

    private fun getAccessTokenUrlObserver(): DisposableObserver<ResponseBody> {
        return object : DisposableObserver<ResponseBody>() {
            override fun onComplete() {
            }

            override fun onNext(responseBody: ResponseBody?) {
                val responseBodyString = responseBody?.string()
                if (responseBody != null) {
                    if (mAuthRepositoryListener != null) {
                        mAuthRepositoryListener!!.onAccessTokenReceived(responseBodyString!!)
                    }
                } else {
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

    private fun onErrorReceived(errorMessage: String) {
        if (mAuthRepositoryListener != null) {
            mAuthRepositoryListener?.onErrorReceived(errorMessage)
        }
    }

    interface AuthRepositoryListener {

        fun onAuthTokenReceived(responseString: String)

        fun onAccessTokenReceived(responseString: String)

        fun onErrorReceived(errorMessage: String)

    }

    fun setAuthRepositoryListener(authRepositoryListener: AuthRepositoryListener?) {
        mAuthRepositoryListener = authRepositoryListener
    }

}