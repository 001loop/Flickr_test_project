package com.mudrichenko.evgeniy.flickrtestproject.ui.profile;

import android.os.Bundle;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment;

public class ProfileFragment extends BaseFragment implements ProfileView, ViewPager.OnPageChangeListener {

    @InjectPresenter
    ProfilePresenter mProfilePresenter;

    ViewPager mViewPager;

    PagerTitleStrip mPagerTitleStrip;

    private final static String MAIN_MENU_FRAGMENT_SAVED_FRAGMENT = "main_menu_fragment_saved_fragment";

    private final static int DEFAULT_FRAGMENT_ID = 0;

    private final static int NUM_OF_PAGES = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mViewPager = view.findViewById(R.id.view_pager);
        mPagerTitleStrip = view.findViewById(R.id.pager_title_strip);
        mViewPager.setAdapter(mProfilePresenter.getPagerAdapter(getChildFragmentManager()));
        mViewPager.setOffscreenPageLimit(NUM_OF_PAGES - 1);
        mViewPager.addOnPageChangeListener(this);
        if (savedInstanceState != null) {
            mProfilePresenter.setViewPagerCurrentPosition(savedInstanceState.getInt(MAIN_MENU_FRAGMENT_SAVED_FRAGMENT, DEFAULT_FRAGMENT_ID));
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewPager.setCurrentItem(mProfilePresenter.getViewPagerCurrentPosition());
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(MAIN_MENU_FRAGMENT_SAVED_FRAGMENT, mViewPager.getCurrentItem());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mProfilePresenter.onViewPagerScrolled(position, positionOffset);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
