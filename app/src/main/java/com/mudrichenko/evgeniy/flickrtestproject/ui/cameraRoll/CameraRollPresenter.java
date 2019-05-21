package com.mudrichenko.evgeniy.flickrtestproject.ui.cameraRoll;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.AppConstants;
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.BasePhotosRepository;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.CameraRollPhotosRepository;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto;
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils;
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@InjectViewState
public class CameraRollPresenter extends MvpPresenter <CameraRollView> implements BasePhotosRepository.RepositoryListener {

    @Inject
    NetworkUtils mNetworkUtils;

    @Inject
    AuthUtils mAuthUtils;

    @Inject
    CameraRollPhotosRepository mCameraRollPhotosRepository;

    List<FlickrPhoto> mFlickrPhotoList;

    private int mCurrentPage;

    private boolean isPageLoaded;

    public boolean isLastPageReached;

    private int mFullscreenPhotoIndex;

    private boolean mIsInternetConnectionAvailable;

    protected void onFirstViewAttach() {
        // todo maybe check internet
        mIsInternetConnectionAvailable = true;
        loadFirstPage();
    }

    public void loadFirstPage() {
        getViewState().resetPhotoList();
        getViewState().showProgressWheel();
        mCurrentPage = 1;
        isPageLoaded = false;
        isLastPageReached = false;
        onPhotoListLoad();
    }

    public CameraRollPresenter() {
        App.Companion.getAppComponent().inject(this);
    }

    public void onPhotoListLoad() {
        mCameraRollPhotosRepository.setRepositoryListener(this);
        mCameraRollPhotosRepository.startLoadPhotosListTask(mCurrentPage, mAuthUtils.getUserPhotosUrl(mCurrentPage, AppConstants.NUM_OF_PHOTOS_ON_PAGE, null));
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

    public void listBottomReached() {
        if (!isLastPageReached && mIsInternetConnectionAvailable) {
            getViewState().showRefreshWheel();
        }
    }

    public void clickedOnPhoto(int index) {
        mFullscreenPhotoIndex = index;
        getViewState().showPhotoFullscreenFragment(mFullscreenPhotoIndex);
    }

    public int getFullscreenPhotoIndex() {
        return mFullscreenPhotoIndex;
    }

    @Override
    public void onPhotoListUploadingFinishedListener(List<FlickrPhoto> flickrPhotoList) {
        mFlickrPhotoList = flickrPhotoList;
        isPageLoaded = true;
        Logger.i("CameraRollPresenter onPhotoSizesUploadingFinishedListener");
        ArrayList<RecyclerViewItem> recyclerViewItems = new ArrayList<>();
        for (int x = 0; x < mFlickrPhotoList.size(); x ++) {
            recyclerViewItems.add(new RecyclerViewItem(PhotosRecyclerViewAdapter.Companion.getViewTypeItem(), mFlickrPhotoList.get(x)));
        }
        if (mFlickrPhotoList.size() == 0 && !mNetworkUtils.isInternetConnectionAvailable()) {
            getViewState().showSnackbarMessage(App.Companion.getAppContext().getString(R.string.no_internet_connection));
        } else if (mFlickrPhotoList.size() > 0){
            if (mFlickrPhotoList.get(0).getPage() == mCurrentPage) {
                getViewState().showPhotos(recyclerViewItems);
            }
        }
        getViewState().hideProgressWheel();
        if (isLastPageReached) {
            getViewState().lastPageReached();
        }
    }

    @Override
    public void onErrorReceived(String errorMessage) {
        if (mFlickrPhotoList == null || mFlickrPhotoList.size() == 0) {
            getViewState().showInfoMessage(errorMessage);
        }
        getViewState().hideProgressWheel();
    }

    public void unsubscribe() {
        mCameraRollPhotosRepository.unsubscribe();
    }

    @Override
    public void onLastPageReached() {
        isLastPageReached = true;
    }

    @Override
    public void onInternetConnectionProblemNotify() {
        mIsInternetConnectionAvailable = false;
    }

}
