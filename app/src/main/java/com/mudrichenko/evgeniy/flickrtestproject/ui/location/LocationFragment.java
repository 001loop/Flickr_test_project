package com.mudrichenko.evgeniy.flickrtestproject.ui.location;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.EndlessScrollListener;
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto;
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment;
import com.mudrichenko.evgeniy.flickrtestproject.ui.map.MapFragment;
import com.mudrichenko.evgeniy.flickrtestproject.ui.photoFullscreen.PhotoFullscreenFragment;
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils;
import com.orhanobut.logger.Logger;
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LocationFragment extends BaseFragment implements LocationView,
        PhotosRecyclerViewAdapter.OnItemClickListener, PhotoFullscreenFragment.PhotoFullscreenFragmentListener,
        SwipeRefreshLayout.OnRefreshListener, MapFragment.MapFragmentListener, View.OnClickListener {

    @InjectPresenter
    LocationPresenter mLocationPresenter;

    @Inject
    PrefUtils mPrefUtils;

    MapFragment mMapFragment;

    private RelativeLayout mMainLayout;

    private ProgressWheel mProgressWheel;

    ArrayList<RecyclerViewItem> mRecyclerViewItems;

    PhotosRecyclerViewAdapter mPhotosAdapter;

    TextView mTextViewInfo;

    RecyclerView mPhotosRecyclerView;

    SwipeRefreshLayout mSwipeRefreshLayout;

    EndlessScrollListener mScrollListener;

    private final int NUM_OF_COLUMNS = 2;

    Button mBtnSetLocation;

    TextView mTextViewLocationName;

    String mMapFragmentTag = "mapFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        App.Companion.getAppComponent().inject(this);
        mMainLayout = view.findViewById(R.id.main_layout);
        mProgressWheel = view.findViewById(R.id.progress_wheel);
        mProgressWheel.setVisibility(View.INVISIBLE);
        mTextViewInfo = view.findViewById(R.id.text_view_info);
        // recyclerView elements
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerViewItems = new ArrayList<>();
        mPhotosAdapter = new PhotosRecyclerViewAdapter(getContext(), mRecyclerViewItems);
        mPhotosAdapter.setOnItemClickListener(this);
        mPhotosRecyclerView = view.findViewById(R.id.recycler_view_photos);
        mPhotosRecyclerView.setAdapter(mPhotosAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), NUM_OF_COLUMNS);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mRecyclerViewItems.get(position).getViewTypeId() == PhotosRecyclerViewAdapter.Companion.getViewTypeBottomText()) {
                    return NUM_OF_COLUMNS;
                }
                return 1;
            }
        });
        mPhotosRecyclerView.setLayoutManager(gridLayoutManager);
        mScrollListener = new EndlessScrollListener(gridLayoutManager) {
            @Override
            public void onBottomReached() {
                if (!mLocationPresenter.isLastPageReached) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            }
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mLocationPresenter.loadMorePhotos();
            }
        };
        mPhotosRecyclerView.addOnScrollListener(mScrollListener);
        // map
        Logger.i("Info; LocationFragment onCreateView");

        mBtnSetLocation = view.findViewById(R.id.btn_set_location);
        mBtnSetLocation.setOnClickListener(this);
        mTextViewLocationName = view.findViewById(R.id.text_view_location_name);
        mTextViewLocationName.setText(mPrefUtils.getLocationName());
        if (savedInstanceState != null) {
            Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(mMapFragmentTag);
            if (fragment instanceof MapFragment) {
                Logger.i("Info; restore fragment and listeners");
                mMapFragment = (MapFragment) fragment;
                mMapFragment.setMapFragmentListener(this);
            }
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.i("Info; LocationFragment onDestroyView");
        mLocationPresenter.unsubscribe();
        if (mMapFragment != null) {
            mMapFragment.setMapFragmentListener(null);
            mMapFragment = null;
        }
    }

    public static LocationFragment newInstance() {
        Logger.i("Info; LocationFragment newInstance()");
        LocationFragment fragment = new LocationFragment();
        /*
        Bundle bundle = new Bundle();
        bundle.putBoolean("isMapFragmentOpened", isMapFragmentOpened);
        fragment.setArguments(bundle);
        */
        return fragment;
    }

    @Override
    public void showProgressWheel() {
        if (mPhotosAdapter.getItemCount() == 0) {
            // there is no items in recyclerView, we can display progressWheel
            mProgressWheel.setVisibility(View.VISIBLE);
            mProgressWheel.startSpinning();
        }
    }

    @Override
    public void hideProgressWheel() {
        mProgressWheel.setVisibility(View.INVISIBLE);
        mProgressWheel.stopSpinning();
    }

    @Override
    public void showPhotoFullscreenFragment(int index) {
        showPhotoFullscreen(mRecyclerViewItems.get(index).getFlickrPhoto());
    }


    @Override
    public void showPhotos(List<RecyclerViewItem> recyclerViewItems) {
        mRecyclerViewItems.addAll(recyclerViewItems);
        mPhotosAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void showPhotoFullscreen(FlickrPhoto flickrPhoto) {
        PhotoFullscreenFragment photoFullscreenFragment = PhotoFullscreenFragment.newInstance(flickrPhoto);
        photoFullscreenFragment.showFragment(getActivity(), R.id.main_menu_layout, false);
        photoFullscreenFragment.setPhotoFullscreenFragmentListener(this);
    }

    @Override
    public void showInfoMessage(String message) {
        mTextViewInfo.setVisibility(View.VISIBLE);
        mTextViewInfo.setText(message);
    }

    @Override
    public void hideInfoMessage() {
        mTextViewInfo.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setLocationName(String locationName) {
        Logger.i("LocationFragment; setLocationName");
        mTextViewLocationName.setText(locationName);
    }

    private void clickOnSetLocation() {
        showMapFragment();
    }

    private void showMapFragment() {
        mMapFragment = MapFragment.newInstance(mLocationPresenter.getLatLng());
        mMapFragment.showFragment(getActivity(), R.id.main_menu_layout, false, mMapFragmentTag);
        mMapFragment.setMapFragmentListener(this);
    }

    @Override
    public void resetPhotoList() {
        mRecyclerViewItems.clear();
        mPhotosAdapter.notifyDataSetChanged();
        mScrollListener.resetState();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_set_location:
                clickOnSetLocation();
                break;
        }
    }

    @Override
    public void onCoordinatesSelectedListener(double lat, double lng, String locationName) {
        resetPhotoList();
        mLocationPresenter.onCoordinatesSelected(lat, lng, locationName);
    }

    @Override
    public void onCancelClickListener() {
        mLocationPresenter.onMapCancelClicked();
    }

    @Override
    public void onItemClick(int position) {
        if (mRecyclerViewItems != null) {
            if (mRecyclerViewItems.size() > position) {
                mLocationPresenter.clickedOnPhoto(position);
            }
        }
    }

    @Override
    public void lastPageReached() {
        mRecyclerViewItems.add(new RecyclerViewItem(PhotosRecyclerViewAdapter.Companion.getViewTypeBottomText(), null));
        mPhotosAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mLocationPresenter.loadFirstPage();
    }

    @Override
    public void onPhotoDeleted() {
        // nothing. We cant delete another user photos
    }

}
