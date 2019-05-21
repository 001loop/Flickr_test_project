package com.mudrichenko.evgeniy.flickrtestproject.ui.photoInfo;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto;
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment;
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils;
import com.mudrichenko.evgeniy.flickrtestproject.utils.StringUtils;
import com.todddavies.components.progressbar.ProgressWheel;

import javax.inject.Inject;

public class PhotoInfoFragment extends BaseFragment implements PhotoInfoView, View.OnClickListener {

    @InjectPresenter
    PhotoInfoPresenter mPhotoInfoPresenter;

    @Inject
    StringUtils mStringUtils;

    @Inject
    PrefUtils mPrefUtils;

    PublicPhotoPresenterListener mPublicPhotoPresenterListener;

    private long mPhotoId;

    private RelativeLayout mInfoLayout;
    private ProgressWheel mProgressWheel;

    private ImageView mButtonViewClose;
    private ImageView mButtonViewDelete;

    TextView mTextViewInfo;

    private TextView mTextViewPhotoName;
    private TextView mTextViewDescription;
    private TextView mTextViewLocation;
    private TextView mTextViewTakenBy;
    private TextView mTextViewDateTaken;
    private TextView mTextViewTags;
    private TextView mTextViewLicense;
    private TextView mTextViewPrivacy;
    private TextView mTextViewViewsCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_info, container, false);
        App.Companion.getAppComponent().inject(this);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        mInfoLayout = view.findViewById(R.id.infoLayout);
        mProgressWheel = view.findViewById(R.id.progress_wheel);
        mTextViewInfo = view.findViewById(R.id.text_view_info);
        mButtonViewClose = view.findViewById(R.id.btnViewClose);
        mButtonViewClose.setOnClickListener(this);
        mButtonViewDelete = view.findViewById(R.id.btnViewDelete);
        mButtonViewDelete.setOnClickListener(this);
        mButtonViewDelete.setVisibility(View.INVISIBLE);
        mTextViewPhotoName = view.findViewById(R.id.textViewPhotoName);
        mTextViewDescription = view.findViewById(R.id.textViewDescription);
        mTextViewLocation = view.findViewById(R.id.textViewLocation);
        mTextViewTakenBy = view.findViewById(R.id.textViewTakenBy);
        mTextViewDateTaken = view.findViewById(R.id.textViewDateTaken);
        mTextViewTags = view.findViewById(R.id.textViewTags);
        mTextViewLicense = view.findViewById(R.id.textViewLicense);
        mTextViewPrivacy = view.findViewById(R.id.textViewPrivacy);
        mTextViewViewsCount = view.findViewById(R.id.textViewViewsCount);
        if (getArguments() != null) {
            mPhotoId = getArguments().getLong("mPhotoId");
        }
        mPhotoInfoPresenter.onPhotoInfoLoad(mPhotoId);
        return view;
    }

    public static PhotoInfoFragment newInstance(Long mPhotoId) {
        PhotoInfoFragment fragment = new PhotoInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("mPhotoId", mPhotoId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void showPhotoInfo(FlickrPhoto flickrPhoto) {
        String mPhotoOwnerNSID = flickrPhoto.getOwnerId();
        if (mPhotoOwnerNSID != null) {
            if (mPhotoOwnerNSID.equals(mPrefUtils.getUserId())) {
                mButtonViewDelete.setVisibility(View.VISIBLE);
            }
        }
        mTextViewPhotoName.setText(flickrPhoto.getName());
        mTextViewDescription.setText(flickrPhoto.getDescription());
        //mTextViewLocation.setText(mStringUtils.getLocationName(flickrPhoto.locgetLocation()));
        mTextViewTakenBy.setText(mStringUtils.getOwnerName(flickrPhoto.getOwnerName(), flickrPhoto.getOwnerRealName()));
        mTextViewDateTaken.setText(flickrPhoto.getDateTaken());
        //mTextViewTags.setText(mStringUtils.getTagsString(flickrPhoto.getTags().getTag()));
        //mTextViewLicense.setText(mStringUtils.getLicenseStringById(flickrPhoto.getLicense()));
        mTextViewPrivacy.setText(mStringUtils.getPrivacyStringById(flickrPhoto.getSafetyLevel()));
        mTextViewViewsCount.setText(String.valueOf(flickrPhoto.getNumOfViews()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPhotoInfoPresenter.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPhotoInfoPresenter.unsubscribe();
    }

    @Override
    public void showProgressWheel() {
        mInfoLayout.setVisibility(View.VISIBLE);
        mTextViewInfo.setVisibility(View.INVISIBLE);
        mProgressWheel.setVisibility(View.VISIBLE);
        mProgressWheel.startSpinning();
    }

    @Override
    public void hideProgressWheel() {
        mInfoLayout.setVisibility(View.INVISIBLE);
        mProgressWheel.setVisibility(View.INVISIBLE);
        mProgressWheel.stopSpinning();
    }

    @Override
    public void showInfoMessage(String message) {
        mInfoLayout.setVisibility(View.VISIBLE);
        mProgressWheel.setVisibility(View.INVISIBLE);
        mTextViewInfo.setVisibility(View.VISIBLE);
        mTextViewInfo.setText(message);
    }

    @Override
    public void hideInfoMessage() {
        mInfoLayout.setVisibility(View.INVISIBLE);
        mTextViewInfo.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPhotoDeleted() {
        if (mPublicPhotoPresenterListener != null) {
            mPublicPhotoPresenterListener.onPhotoDeleted();
        }
        removeFragment();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnViewClose:
                clickOnBtnClose();
                break;
            case R.id.btnViewDelete:
                clickOnBtnDelete();
                break;
        }
    }

    private void clickOnBtnClose() {
        removeFragment();
    }

    private void removeFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }

    private void clickOnBtnDelete() {
        mPhotoInfoPresenter.onButtonDeleteClick(mPhotoId);
    }

    public interface PublicPhotoPresenterListener {

        void onPhotoDeleted();

    }

    public void setPublicPhotoPresenterListener(PublicPhotoPresenterListener publicPhotoPresenterListener) {
        mPublicPhotoPresenterListener = publicPhotoPresenterListener;
    }


}
