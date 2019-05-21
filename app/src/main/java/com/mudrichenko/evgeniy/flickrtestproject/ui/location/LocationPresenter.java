package com.mudrichenko.evgeniy.flickrtestproject.ui.location;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.AppConstants;
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.BasePhotosRepository;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.LocationPhotosRepository;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto;
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils;
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils;
import com.google.android.gms.maps.model.LatLng;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@InjectViewState
public class LocationPresenter extends MvpPresenter<LocationView>
        implements BasePhotosRepository.RepositoryListener {

    @Inject
    LocationPhotosRepository mLocationPhotosRepository;

    @Inject
    AuthUtils mAuthUtils;

    @Inject
    PrefUtils mPrefUtils;

    private int mCurrentPage;

    private boolean isPageLoaded;

    public boolean isLastPageReached;

    private int mFullscreenPhotoIndex;

    private final String LOCATION_CODE_NOT_SELECTED = "0";

    private LatLng mLatLng;

    private String mLocationName;

    private boolean mIsFirstEntry;

    protected void onFirstViewAttach() {
        loadFirstEntryData();
    }

    public void loadFirstPage() {
        getViewState().resetPhotoList();
        getViewState().showProgressWheel();
        mCurrentPage = 1;
        isPageLoaded = false;
        isLastPageReached = false;
        onPhotoListLoad();
    }

    public void loadFirstEntryData() {
        Logger.i("onFirstViewAttach");
        Logger.i("firstAttach; num of views = " + getAttachedViews().size());
        mCurrentPage = 1;
        isPageLoaded = false;
        loadData();
        if (mIsFirstEntry) {
            Logger.i("mIsFirstEntry");
            getViewState().showInfoMessage(App.Companion.getAppContext().getString(R.string.location_hint_for_user));
        } else {
            getViewState().showProgressWheel();
            getViewState().hideInfoMessage();
            onPhotoListLoad();
        }
    }

    public LocationPresenter() {
        App.Companion.getAppComponent().inject(this);
    }

    public void loadMorePhotos() {
        if (isLastPageReached) {
            return;
        }
        if (isPageLoaded) {
            mCurrentPage++;
            onPhotoListLoad();
        }
    }

    public void clickedOnPhoto(int index) {
        mFullscreenPhotoIndex = index;
        getViewState().showPhotoFullscreenFragment(mFullscreenPhotoIndex);
    }

    public void onPhotoListLoad() {
        mLocationPhotosRepository.setRepositoryListener(this);
        mLocationPhotosRepository.startLoadPhotosListTask(mCurrentPage, mAuthUtils.getLocationPhotosUrl(mCurrentPage, AppConstants.NUM_OF_PHOTOS_ON_PAGE, mLatLng.latitude, mLatLng.longitude));
    }

    @Override
    public void onPhotoListUploadingFinishedListener(List<FlickrPhoto> flickrPhotoList) {
        isPageLoaded = true;
        Logger.i(getClass().getName() + " onPhotoListUploadingFinishedListener");
        ArrayList<RecyclerViewItem> recyclerViewItems = new ArrayList<>();
        for (int x = 0; x < flickrPhotoList.size(); x ++) {
            recyclerViewItems.add(new RecyclerViewItem(PhotosRecyclerViewAdapter.Companion.getViewTypeItem(), flickrPhotoList.get(x)));
        }
        if (flickrPhotoList.size() > 0 && (flickrPhotoList.get(0).getPage() == mCurrentPage)) {
            getViewState().showPhotos(recyclerViewItems);
        }
        getViewState().hideProgressWheel();
        if (isLastPageReached) {
            getViewState().lastPageReached();
        }
    }

    @Override
    public void onErrorReceived(String errorMessage) {
        getViewState().showInfoMessage(errorMessage);
    }

    public void unsubscribe() {
        mLocationPhotosRepository.unsubscribe();
    }

    @Override
    public void onLastPageReached() {
        isLastPageReached = true;
    }

    private void loadData() {
        mLocationName = mPrefUtils.getLocationName();
        if (mLocationName.equals("")) {
            mLocationName = App.Companion.getAppContext().getString(R.string.location_name);
        }
        LatLng savedLatLng = mPrefUtils.getLocationLatLng();
        if (savedLatLng == null) {
            mIsFirstEntry = true;
            mLocationName = LOCATION_CODE_NOT_SELECTED;
        } else {
            mIsFirstEntry = false;
            mLatLng = savedLatLng;
            Logger.i("location presenter mLatLng = " + mLatLng.latitude + " ; " + mLatLng.longitude);
        }
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void onCoordinatesSelected(double lat, double lng, String locationName) {
        mCurrentPage = 1;
        mLocationName = locationName;
        mLatLng = new LatLng(lat, lng);
        mPrefUtils.putLocationName(mLocationName);
        Logger.i("location presenter saved locationName = " + locationName);
        mPrefUtils.putLocationLatLng(mLatLng);
        if (mLocationName != null) {
            Logger.i("num of views = " + getAttachedViews().size());
            // todo fix bug
            // mapFragment opened -> screen rotated -> coordinates selected -> apply clicked -> getViewState == 0,
            // and after second screen rotation, new photos uploaded to locationFragment
            getViewState().setLocationName(mLocationName);
        }
        onPhotoListLoad();
    }

    public void onMapCancelClicked() {
        String locationName = mPrefUtils.getLocationName();
        getViewState().setLocationName(locationName);
    }

    @Override
    public void onInternetConnectionProblemNotify() {

    }
}
