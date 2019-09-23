package com.mudrichenko.evgeniy.flickrtestproject.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.logger.Logger;

public class MapFragment extends BaseFragment implements MapView,
        OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMapClickListener {

    @InjectPresenter
    MapPresenter mMapPresenter;

    private GoogleMap mMap;

    private Marker mMarker;

    private TextView mTextViewLocationName;

    private MapFragmentListener mMapFragmentListener;

    private final int ACCESS_COARSE_LOCATION = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        if (savedInstanceState != null) {
            mMapPresenter.setLatLng(savedInstanceState);
        } else {
            mMapPresenter.setLatLng(getArguments());
        }
        mapFragment.getMapAsync(this);
        Button btnApply = view.findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(this);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        mTextViewLocationName = view.findViewById(R.id.textViewLocationName);


        return view;
    }

    public static MapFragment newInstance(LatLng latLng) {
        Logger.i("MapFragment newInstance");
        MapFragment fragment = new MapFragment();
        if (latLng != null) {
            Bundle bundle = new Bundle();
            bundle.putDouble("lat", latLng.latitude);
            bundle.putDouble("lon", latLng.longitude);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapPresenter.unsubscribe();
        mMapPresenter.onViewDestroy();
        mMapFragmentListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        LatLng savedLatLng = mMapPresenter.getSavedLatLng();
        if (savedLatLng != null) {
            bundle.putDouble("lat", savedLatLng.latitude);
            bundle.putDouble("lon", savedLatLng.longitude);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_apply:
                clickOnApply();
                break;
            case R.id.btn_cancel:
                clickOnCancel();
        }
    }

    private void clickOnApply() {
        LatLng latLng = null;
        if (mMarker != null) {
            latLng = mMarker.getPosition();
        }
        mMapPresenter.clickOnCoordinatesApply(latLng);
    }


    private void clickOnCancel() {
        if (mMapFragmentListener != null) {
            mMapFragmentListener.onCancelClickListener();
        }
        removeFragment();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        mMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        mMapPresenter.clickOnMap(latLng);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng savedLatLng = mMapPresenter.getSavedLatLng();
        if (savedLatLng != null) {
            mMarker = mMap.addMarker(new MarkerOptions().position(savedLatLng));
            mMapPresenter.onMapReady();
        }
        setMyLocationEnabled(true);
    }

    @Override
    public void coordinatesApply(double lat, double lng, String locationName) {
        if (mMapFragmentListener != null) {
            mMapFragmentListener.onCoordinatesSelectedListener(lat, lng, locationName);
        } else {
            Logger.i("coordinatesApply; mMapFragmentListener = null");
        }
        removeFragment();
    }

    private void removeFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }

    @Override
    public void newMarkerAdded(String locationName) {
        mTextViewLocationName.setText(locationName);
    }

    @Override
    public void moveMapCamera(CameraUpdate cameraUpdate) {
        // todo exception
        if (mMap != null) {
            mMap.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void addMarker(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng));
    }

    @Override
    public void showMessageNoCoordinates() {
        Toast.makeText(getContext(), getString(R.string.location_error_select_coordinates), Toast.LENGTH_SHORT).show();
    }

    private void setMyLocationEnabled(boolean isNeedToRequestPermission) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (isNeedToRequestPermission) {
                requestLocationPermission();
            }
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
    }

    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                ACCESS_COARSE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setMyLocationEnabled(false);
                }
            }
        }
    }

    public interface MapFragmentListener {

        void onCoordinatesSelectedListener(double lat, double lng, String locationName);

        void onCancelClickListener();

    }

    public void setMapFragmentListener(MapFragmentListener mapFragmentListener) {
        mMapFragmentListener = mapFragmentListener;
    }

}
