package com.mudrichenko.evgeniy.flickrtestproject.ui.publicPhotos;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.AppConstants;
import com.mudrichenko.evgeniy.flickrtestproject.PhotosRecyclerViewAdapter;
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.BasePhotosRepository;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.PublicPhotosRepository;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto;
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils;
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@InjectViewState
public class PublicPhotosPresenter extends MvpPresenter<PublicPhotosView> implements BasePhotosRepository.RepositoryListener {

    @Inject
    NetworkUtils mNetworkUtils;

    @Inject
    AuthUtils mAuthUtils;

    @Inject
    PublicPhotosRepository mPublicPhotosRepository;

    private int mCurrentPage;

    private boolean isPageLoaded;

    public boolean isLastPageReached;

    private int mFullscreenPhotoIndex;

    protected void onFirstViewAttach() {
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

    public PublicPhotosPresenter() {
        App.Companion.getAppComponent().inject(this);
    }

    public void onPhotoListLoad() {
        mPublicPhotosRepository.setRepositoryListener(this);
        mPublicPhotosRepository.startLoadPhotosListTask(mCurrentPage, mAuthUtils.getPublicPhotosUrl(mCurrentPage, AppConstants.NUM_OF_PHOTOS_ON_PAGE));
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

    public int getFullscreenPhotoIndex() {
        return mFullscreenPhotoIndex;
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
        mPublicPhotosRepository.unsubscribe();
    }

    @Override
    public void onLastPageReached() {
        isLastPageReached = true;
    }

    @Override
    public void onInternetConnectionProblemNotify() {

    }
}
