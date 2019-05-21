package com.mudrichenko.evgeniy.flickrtestproject.ui.map;

import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.LocationNameRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

@InjectViewState
public class MapPresenter extends MvpPresenter<MapView> implements LocationNameRepository.LocationNameRepositoryListener {

    @Inject
    LocationNameRepository mLocationNameRepository;

    //private Marker mMarker;
    private double mLat;
    private double mLng;
    private static LatLng savedLatLng;

    private static final int MAP_ZOOM_VALUE = 6;

    public MapPresenter() {
        App.Companion.getAppComponent().inject(this);
    }

    public void clickOnCoordinatesApply(LatLng latLng) {
        if (latLng != null) {
            mLat = latLng.latitude;
            mLng = latLng.longitude;
            getNameTask(latLng, true);
        } else {
            getViewState().showMessageNoCoordinates();
        }
    }

    public void setLatLng(Bundle bundle) {
        if (bundle != null) {
            savedLatLng = new LatLng(bundle.getDouble("lat"), bundle.getDouble("lon"));
        }
    }

    public void onViewDestroy() {
        // todo exception Parameter specified as non-null is null: method kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull, parameter locationNameRepositoryListener
        //mLocationNameRepository.setLocationNameRepositoryListener(null);
    }

    public LatLng getSavedLatLng() {
        return savedLatLng;
    }

    public void onMapReady() {
        if (savedLatLng != null) {
            clickOnMap(savedLatLng);
            getViewState().moveMapCamera(CameraUpdateFactory.newLatLngZoom(savedLatLng, MAP_ZOOM_VALUE));
        }
    }

    public void clickOnMap(LatLng latLng) {
        savedLatLng = latLng;
        getNameTask(latLng, false);
    }

    private void getNameTask(LatLng latLng, boolean isApplyClicked) {
        mLocationNameRepository.setLocationNameRepositoryListener(this);
        mLocationNameRepository.onLocationNameLoad(latLng, isApplyClicked);
    }

    @Override
    public void onLocationNameReceived(String locationName, boolean isApplyClicked) {
        Logger.i("onLocationNameReceived; locationName = " + locationName + "; mIsApplyClicked = " + isApplyClicked);
        Logger.i("num of att viewws = " + getAttachedViews().size());
        if (isApplyClicked) {
            getViewState().coordinatesApply(mLat, mLng, locationName);
        } else {
            getViewState().newMarkerAdded(locationName);
        }
    }

    public void unsubscribe() {
        mLocationNameRepository.unsubscribe();
    }

}
