package com.mudrichenko.evgeniy.flickrtestproject.ui.photoFullscreen;

import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.PhotoFullscreenRepository;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto;
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@InjectViewState
public class PhotoFullscreenPresenter extends MvpPresenter<PhotoFullscreenView> implements PhotoFullscreenRepository.PhotoFullscreenRepositoryListener {

    @Inject
    NetworkUtils mNetworkUtils;

    @Inject
    PhotoFullscreenRepository mPhotoFullscreenRepository;

    private FlickrPhoto mFlickrPhoto;

    public PhotoFullscreenPresenter() {
        App.Companion.getAppComponent().inject(this);
    }

    public void onPhotoLoad(Bundle bundle) {
        getViewState().startLoadingPhoto();
        if (bundle != null) {
            mFlickrPhoto = (FlickrPhoto) bundle.getSerializable("flickrPhoto");
            if (mFlickrPhoto != null) {
                if (mFlickrPhoto.getUrlOriginalSize() != null) {
                    getViewState().showPhoto(mFlickrPhoto, mFlickrPhoto.getUrlOriginalSize());
                } else {
                    if (mNetworkUtils.isInternetConnectionAvailable()) {
                        mPhotoFullscreenRepository.setPhotoFullscreenRepositoryListener(this);
                        mPhotoFullscreenRepository.startLoadImageSizesTask(mFlickrPhoto);
                    } else {
                        getViewState().showPhoto(mFlickrPhoto, mFlickrPhoto.getUrl());
                    }
                }
            }
        }
    }

    public void unsubscribe() {
        mPhotoFullscreenRepository.unsubscribe();
    }

    @Override
    public void onErrorReceived() {
        // nothing to do with error, just show low-resolution image
        getViewState().showPhoto(mFlickrPhoto, mFlickrPhoto.getUrl());
    }

    @Override
    public void onPhotoSizesUploadingFinishedListener(@NotNull FlickrPhoto flickrPhoto) {
        mFlickrPhoto = flickrPhoto;
        if (mFlickrPhoto.getUrlOriginalSize() != null) {
            getViewState().showPhoto(mFlickrPhoto, mFlickrPhoto.getUrlOriginalSize());
        } else {
            getViewState().showPhoto(mFlickrPhoto, mFlickrPhoto.getUrl());
        }
    }

}
