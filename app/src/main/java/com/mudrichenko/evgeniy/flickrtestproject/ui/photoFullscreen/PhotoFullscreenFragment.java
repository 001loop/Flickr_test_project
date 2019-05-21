package com.mudrichenko.evgeniy.flickrtestproject.ui.photoFullscreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.AppConstants;
import com.mudrichenko.evgeniy.flickrtestproject.GlideApp;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto;
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment;
import com.mudrichenko.evgeniy.flickrtestproject.ui.photoInfo.PhotoInfoFragment;
import com.mudrichenko.evgeniy.flickrtestproject.utils.ImageUtils;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.orhanobut.logger.Logger;
import com.todddavies.components.progressbar.ProgressWheel;

import javax.inject.Inject;

public class PhotoFullscreenFragment extends BaseFragment implements
        PhotoFullscreenView, View.OnClickListener,
        PhotoInfoFragment.PublicPhotoPresenterListener, OnViewTapListener {

    @Inject
    ImageUtils mImageUtils;

    @InjectPresenter
    PhotoFullscreenPresenter mPhotoFullscreenPresenter;

    PhotoFullscreenFragmentListener mPhotoFullscreenFragmentListener;

    ConstraintLayout mOverlayTop;
    ConstraintLayout mOverlayBot;

    ImageView mBtnViewClose;
    ImageView mBtnViewInfo;
    ImageView mBtnViewShare;
    ImageView mBtnViewToFlickr;
    ImageView mBtnViewDownload;
    TextView mTextViewPhotoName;
    PhotoView mPhotoView;

    ProgressWheel mProgressBar;

    private boolean isUiVisible;

    private boolean isFirstTimeAnimation;

    private boolean isFirstTimeAnimationEnded;

    private boolean isPhotoDataAvailable;

    private boolean isButtonsVisible;

    private FlickrPhoto mFlickrPhoto;

    private Animation fadeInAnimation;
    private Animation fadeOutFirstAnimation;
    private Animation fadeOutAnimation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_fullscreen, container, false);
        view.setOnTouchListener((view1, motionEvent) -> true);
        mOverlayTop = view.findViewById(R.id.photoInfoOverlayTop);
        mOverlayBot = view.findViewById(R.id.photoInfoOverlayBot);
        mPhotoView = view.findViewById(R.id.imageViewPhoto);
        mPhotoView.setMaximumScale(AppConstants.PHOTO_VIEW_MAX_SCALE);
        mPhotoView.setOnViewTapListener(this);
        view.setOnClickListener(view12 -> {
        });
        mTextViewPhotoName = view.findViewById(R.id.textViewPhotoName);
        mBtnViewClose = view.findViewById(R.id.btnViewClose);
        mBtnViewClose.setOnClickListener(this);
        mBtnViewInfo = view.findViewById(R.id.btnViewInfo);
        mBtnViewInfo.setOnClickListener(this);
        mBtnViewShare = view.findViewById(R.id.btnViewShare);
        mBtnViewShare.setOnClickListener(this);
        mBtnViewToFlickr = view.findViewById(R.id.btnViewToFlickr);
        mBtnViewToFlickr.setOnClickListener(this);
        mBtnViewDownload = view.findViewById(R.id.btnViewDownload);
        mBtnViewDownload.setOnClickListener(this);
        mProgressBar = view.findViewById(R.id.progress_wheel);
        mProgressBar.startSpinning();
        mPhotoFullscreenPresenter.onPhotoLoad(getArguments());
        isFirstTimeAnimation = true;
        isPhotoDataAvailable = false;
        mBtnViewInfo.setClickable(isPhotoDataAvailable);
        mBtnViewShare.setClickable(isPhotoDataAvailable);
        mBtnViewDownload.setClickable(isPhotoDataAvailable);
        setDefaultUiVisibility();
        return view;
    }

    public PhotoFullscreenFragment() {
        App.Companion.getAppComponent().inject(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPhotoFullscreenPresenter.unsubscribe();
    }

    private void hideOverlay() {
        Context context = getContext();
        if (context == null || mOverlayTop == null || mOverlayBot == null) {
            return;
        }
        isUiVisible = false;
        if (isFirstTimeAnimation) {
            isButtonsVisible = true;
            isFirstTimeAnimationEnded = false;
            fadeOutFirstAnimation = AnimationUtils.loadAnimation(context, R.anim.fullscreen_photo_overlay_fadeout_first);
            fadeOutFirstAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    isFirstTimeAnimationEnded = true;
                    isButtonsVisible = false;
                    changeButtonsLock();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mOverlayTop.startAnimation(fadeOutFirstAnimation);
            mOverlayBot.startAnimation(fadeOutFirstAnimation);
        } else {
            fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fullscreen_photo_overlay_fadeout);
            fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    isButtonsVisible = false;
                    changeButtonsLock();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mOverlayTop.startAnimation(fadeOutAnimation);
            mOverlayBot.startAnimation(fadeOutAnimation);
        }
        isFirstTimeAnimation = false;
    }

    private void cancelFirstTimeAnimation() {
        mOverlayTop.clearAnimation();
        mOverlayBot.clearAnimation();
        isUiVisible = true;
    }

    private void showOverlay() {
        Context context = getContext();
        if (context == null || mOverlayTop == null || mOverlayBot == null) {
            return;
        }
        isUiVisible = true;
        fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fullscreen_photo_overlay_fadein);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                isButtonsVisible = true;
                changeButtonsLock();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mOverlayTop.startAnimation(fadeInAnimation);
        mOverlayBot.startAnimation(fadeInAnimation);
    }

    private void changeUiVisibility() {
        if (!isFirstTimeAnimationEnded) {
            cancelFirstTimeAnimation();
            return;
        }
        if (isUiVisible) {
            hideOverlay();
        } else {
            showOverlay();
        }
    }

    private void setDefaultUiVisibility() {
        hideOverlay();
    }

    public static PhotoFullscreenFragment newInstance(FlickrPhoto flickrPhoto) {
        PhotoFullscreenFragment fragment = new PhotoFullscreenFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("flickrPhoto", flickrPhoto);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnViewClose:
                clickOnBtnClose();
                break;
            case R.id.btnViewInfo:
                clickOnBtnInfo();
                break;
            case R.id.btnViewShare:
                clickOnBtnShare();
                break;
            case R.id.btnViewToFlickr:
                clickOnBtnToFlickr();
                break;
            case R.id.btnViewDownload:
                clickOnBtnDownload();
                break;
        }
    }

    private void clickOnBtnInfo() {
        PhotoInfoFragment photoInfoFragment = PhotoInfoFragment.newInstance(mFlickrPhoto.getFlickrId());
        photoInfoFragment.showFragment(getActivity(), R.id.main_menu_layout, false);
        photoInfoFragment.setPublicPhotoPresenterListener(this);
    }

    private void clickOnBtnClose() {
        removeFragment();
    }

    private void clickOnBtnShare() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mFlickrPhoto.getUrlOriginalSize());
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_intent_message)));
    }

    private void clickOnBtnToFlickr() {
        // todo get link to flick. probably we need to add this into db
    }

    private void clickOnBtnDownload() {
        // todo download original size
        Logger.i("original size = " + mFlickrPhoto.getUrlOriginalSize());
        Logger.i("default size = " + mFlickrPhoto.getUrl());
        if (mFlickrPhoto.getUrlOriginalSize() != null) {
            mImageUtils.getBitmapFromUrl(mFlickrPhoto.getUrlOriginalSize());
        }
    }

    @Override
    public void showPhoto(@NonNull FlickrPhoto flickrPhoto, @NonNull String imageUrl) {
        mFlickrPhoto = flickrPhoto;
        GlideApp.with(this).
                load(imageUrl)
                .placeholder(getResources().getDrawable(R.drawable.image_default))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        onPhotoReady();
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        onPhotoReady();
                        return false;
                    }
                })
                .into(mPhotoView);
        mTextViewPhotoName.setText(flickrPhoto.getName());
    }

    private void onPhotoStartLoading() {
        mPhotoView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void onPhotoReady() {
        isPhotoDataAvailable = true;
        changeButtonsLock();
        mPhotoView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void changeButtonsLock() {
        mBtnViewClose.setClickable(isButtonsVisible);
        mBtnViewShare.setClickable(isButtonsVisible);
        mBtnViewToFlickr.setClickable(isButtonsVisible);
        mBtnViewDownload.setClickable(isButtonsVisible);
        if (isButtonsVisible && isPhotoDataAvailable) {
            mBtnViewInfo.setClickable(true);
            mBtnViewShare.setClickable(true);
            mBtnViewDownload.setClickable(true);
        } else {
            mBtnViewInfo.setClickable(false);
            mBtnViewShare.setClickable(false);
            mBtnViewDownload.setClickable(false);
        }
    }

    @Override
    public void onPhotoDeleted() {
        if (mPhotoFullscreenFragmentListener != null) {
            mPhotoFullscreenFragmentListener.onPhotoDeleted();
        }
        removeFragment();
    }

    private void removeFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        changeUiVisibility();
    }

    @Override
    public void startLoadingPhoto() {
        onPhotoStartLoading();
    }

    public interface PhotoFullscreenFragmentListener {
        void onPhotoDeleted();
    }

    public void setPhotoFullscreenFragmentListener(PhotoFullscreenFragmentListener photoFullscreenFragmentListener) {
        mPhotoFullscreenFragmentListener = photoFullscreenFragmentListener;
    }

}
