package com.mudrichenko.evgeniy.flickrtestproject.ui.map

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

@StateStrategyType(AddToEndStrategy::class)
interface MapView : MvpView {

    fun coordinatesApply(lat: Double, lng: Double, locationName: String)

    fun newMarkerAdded(locationName: String)

    fun moveMapCamera(cameraUpdate: CameraUpdate)

    fun addMarker(latLng: LatLng)

    fun showMessageNoCoordinates()

}
