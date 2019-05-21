package com.mudrichenko.evgeniy.flickrtestproject.ui.photoInfo;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.PhotoInfoRepository;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto;
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@InjectViewState
public class PhotoInfoPresenter extends MvpPresenter<PhotoInfoView>
        implements PhotoInfoRepository.PhotoInfoRepositoryListener {

    @Inject
    PhotoInfoRepository mPhotoInfoRepository;

    @Inject
    NetworkUtils mNetworkUtils;

    public PhotoInfoPresenter() {
        App.Companion.getAppComponent().inject(this);
    }

    public void onPhotoInfoLoad(final long photoId) {
        if (!mNetworkUtils.isInternetConnectionAvailable()) {
            getViewState().showInfoMessage(App.Companion.getAppContext().getResources().getString(R.string.no_internet_connection));
            return;
        }
        getViewState().hideInfoMessage();
        getViewState().showProgressWheel();
        mPhotoInfoRepository.setPhotoInfoRepositoryListener(this);
        mPhotoInfoRepository.onPhotoInfoLoad(photoId);
    }

    public void onButtonDeleteClick(final long photoId) {
        mPhotoInfoRepository.setPhotoInfoRepositoryListener(this);
        mPhotoInfoRepository.onPhotoDeleteFromServer(photoId);
    }

    @Override
    public void onErrorReceived(String errorMessage) {
        getViewState().hideProgressWheel();
        getViewState().showInfoMessage(errorMessage);
    }

    @Override
    public void onPhotoDeleted() {
        Logger.i("PhotoInfoPresenter; onPhotoDeleted");
        getViewState().onPhotoDeleted();
    }

    public void unsubscribe() {
        mPhotoInfoRepository.unsubscribe();
    }

    @Override
    public void onPhotoInfoReceived(@NotNull FlickrPhoto flickrPhoto) {
        getViewState().showPhotoInfo(flickrPhoto);
        getViewState().hideProgressWheel();
        getViewState().hideInfoMessage();
    }

}
