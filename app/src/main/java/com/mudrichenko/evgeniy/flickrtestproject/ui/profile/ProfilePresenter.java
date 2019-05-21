package com.mudrichenko.evgeniy.flickrtestproject.ui.profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.PagerAdapter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.ui.albums.AlbumsFragment;
import com.mudrichenko.evgeniy.flickrtestproject.ui.cameraRoll.CameraRollFragment;
import com.mudrichenko.evgeniy.flickrtestproject.ui.contactList.ContactListFragment;
import com.mudrichenko.evgeniy.flickrtestproject.ui.publicPhotos.PublicPhotosFragment;

@InjectViewState
public class ProfilePresenter extends MvpPresenter<ProfileView> {

    private final static int NUM_OF_PAGES = 4;

    private final int CAMERA_ROLL_FRAGMENT_ID = 0;
    private final int PUBLIC_PHOTOS_FRAGMENT_ID = 1;
    private final int ALBUMS_FRAGMENT_ID = 2;
    private final int CONTACT_LIST_FRAGMENT_ID = 3;

    private final int DEFAULT_FRAGMENT_ID = 0;

    private int mViewPagerCurrentPosition;

    private int backgroundColorCameraRoll;
    private int backgroundColorPublicPhotos;
    private int backgroundColorAlbums;
    private int backgroundColorContactList;

    private CameraRollFragment mCameraRollFragment;
    private PublicPhotosFragment mPublicPhotosFragment;
    private AlbumsFragment mAlbumsFragment;
    private ContactListFragment mContactListFragment;

    public ProfilePresenter() {
        mViewPagerCurrentPosition = DEFAULT_FRAGMENT_ID;
        backgroundColorCameraRoll = ContextCompat.getColor(App.Companion.getAppContext(), R.color.backgroundCameraRoll);
        backgroundColorPublicPhotos = ContextCompat.getColor(App.Companion.getAppContext(), R.color.backgroundPublicPhotos);
        backgroundColorAlbums = ContextCompat.getColor(App.Companion.getAppContext(), R.color.backgroundAlbums);
        backgroundColorContactList = ContextCompat.getColor(App.Companion.getAppContext(), R.color.backgroundContactList);
    }

    public void setViewPagerCurrentPosition(int position) {
        mViewPagerCurrentPosition = position;
    }

    public int getViewPagerCurrentPosition() {
        return mViewPagerCurrentPosition;
    }

    public PagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
        return new MyFragmentPagerAdapter(fragmentManager);
    }

    public void onViewPagerScrolled(int position, float positionOffset) {
        if (position == 0) {
            if (mCameraRollFragment != null && mPublicPhotosFragment != null) {
                int color = ColorUtils.blendARGB(
                        backgroundColorCameraRoll, backgroundColorPublicPhotos, positionOffset);
                mCameraRollFragment.setBackgroundColor(color);
                mPublicPhotosFragment.setBackgroundColor(color);
            }
        } else if (position == 1) {
            if (mPublicPhotosFragment != null && mAlbumsFragment != null) {
                int color = ColorUtils.blendARGB(
                        backgroundColorPublicPhotos, backgroundColorAlbums, positionOffset);
                mPublicPhotosFragment.setBackgroundColor(color);
                mAlbumsFragment.setBackgroundColor(color);
            }
        } else if (position == 2) {
            if (mAlbumsFragment != null && mContactListFragment != null) {
                int color = ColorUtils.blendARGB(
                        backgroundColorAlbums, backgroundColorContactList, positionOffset);
                mAlbumsFragment.setBackgroundColor(color);
                mContactListFragment.setBackgroundColor(color);
            }
        }
    }

    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case CAMERA_ROLL_FRAGMENT_ID:
                    mCameraRollFragment = CameraRollFragment.Companion.newInstance();
                    return mCameraRollFragment;
                case PUBLIC_PHOTOS_FRAGMENT_ID:
                    mPublicPhotosFragment = PublicPhotosFragment.newInstance();
                    return mPublicPhotosFragment;
                case ALBUMS_FRAGMENT_ID:
                    mAlbumsFragment = AlbumsFragment.newInstance();
                    return mAlbumsFragment;
                case CONTACT_LIST_FRAGMENT_ID:
                    mContactListFragment = ContactListFragment.newInstance();
                    return mContactListFragment;
                default:
                    mCameraRollFragment = CameraRollFragment.Companion.newInstance();
                    return mCameraRollFragment;
            }
        }

        @Override
        public int getCount() {
            return NUM_OF_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case CAMERA_ROLL_FRAGMENT_ID:
                    return App.Companion.getAppContext().getString(R.string.camera_roll);
                case PUBLIC_PHOTOS_FRAGMENT_ID:
                    return App.Companion.getAppContext().getString(R.string.public_photos);
                case ALBUMS_FRAGMENT_ID:
                    return App.Companion.getAppContext().getString(R.string.albums);
                case CONTACT_LIST_FRAGMENT_ID:
                    return App.Companion.getAppContext().getString(R.string.contact_list);
                default:
                    return App.Companion.getAppContext().getString(R.string.camera_roll);
            }
        }

    }

}
