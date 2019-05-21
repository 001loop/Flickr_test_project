package com.mudrichenko.evgeniy.flickrtestproject.data.repository

import android.location.Address
import android.location.Geocoder
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.api.ApiConstants
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class LocationNameRepository {

    private var mLocationNameRepositoryListener: LocationNameRepositoryListener? = null

    private var disposables: ArrayList<Disposable>? = null

    private var mIsApplyClicked: Boolean = false

    init {
        App.appComponent!!.inject(this)
        disposables = ArrayList()
    }

    fun onLocationNameLoad(latLng: LatLng, isApplyClicked: Boolean) {
        unsubscribe()
        disposables = ArrayList()
        mIsApplyClicked = isApplyClicked
        val disposable = getLocationNameObservable(latLng)
                .subscribeWith(getLocationNameObserver())
        disposables?.add(disposable)
    }

    private fun getLocationName(latitude: Double, longitude: Double): String {
        val geo = Geocoder(App.appContext, Locale.getDefault())
        val addresses: MutableList<Address>? = geo.getFromLocation(latitude, longitude, 1) ?: return ""
        return if (addresses?.size == 0) {
            ""
        } else
            getResultedLocationName(addresses!![0])
    }

    private fun getResultedLocationName(address: Address): String {
        var locationName = ""
        val featureName = address.featureName
        if (isCorrectLocationName(featureName)) {
            locationName += "$featureName, "
        }
        val localityName = address.locality
        if (isCorrectLocationName(localityName)) {
            locationName += "$localityName, "
        }
        val adminArea = address.adminArea
        if (isCorrectLocationName(adminArea)) {
            locationName += "$adminArea, "
        }
        val countryName = address.countryName
        locationName += countryName
        return locationName
    }

    private fun isCorrectLocationName(locationName: String?): Boolean {
        if (locationName == null) {
            return false
        }
        if (locationName.contains(ApiConstants.GOOGLE_MAPS_LOCATION_NAME_UNNAMED)) {
            return false
        }
        return true
    }

    private fun getLocationNameObservable(latLng: LatLng): Observable<String> {
        return Observable.defer { Observable.just(getLocationName(latLng.latitude, latLng.longitude))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())}
    }

    private fun getLocationNameObserver(): DisposableObserver<String> {
        return object : DisposableObserver<String>() {
            override fun onComplete() {

            }
            override fun onNext(response: String?) {
                if (response == null) {
                    nameReceived("")
                } else {
                    nameReceived(response)
                }
            }
            override fun onError(e: Throwable?) {
                nameReceived("")
            }
        }
    }

    private fun nameReceived(name: String) {
        if (mLocationNameRepositoryListener != null) {
            mLocationNameRepositoryListener!!.onLocationNameReceived(name, mIsApplyClicked)
        }
    }

    fun unsubscribe() {
        if (disposables == null) {
            return
        }
        for (x in disposables!!.indices) {
            val disposable: Disposable = disposables!![x]
            if (!disposable.isDisposed) {
                disposable.dispose()
            }
        }
        disposables?.clear()
    }

    interface LocationNameRepositoryListener {
        fun onLocationNameReceived(locationName: String, isApplyClicked: Boolean)
    }

    fun setLocationNameRepositoryListener(locationNameRepositoryListener: LocationNameRepositoryListener) {
        mLocationNameRepositoryListener = locationNameRepositoryListener
    }

}