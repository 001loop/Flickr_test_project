package com.mudrichenko.evgeniy.flickrtestproject.ui.main;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment;
import com.mudrichenko.evgeniy.flickrtestproject.BottomNavigationViewHelper;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.ui.location.LocationFragment;
import com.mudrichenko.evgeniy.flickrtestproject.ui.options.OptionsFragment;
import com.mudrichenko.evgeniy.flickrtestproject.ui.profile.ProfileFragment;
import com.mudrichenko.evgeniy.flickrtestproject.ui.recent.RecentFragment;
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils;

import javax.inject.Inject;

public class MainActivity extends MvpAppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {


    @Inject
    PrefUtils mPrefUtils;

    private final static String CURRENT_FRAGMENT_ID = "current_fragment_id";

    private final static int BOT_MENU_ITEM_DEFAULT_ID = R.id.navigation_menu_recent;
    private final static int BOT_MENU_ITEM_PROFILE_ID = R.id.navigation_menu_profile;
    private final static int BOT_MENU_ITEM_RECENT_ID = R.id.navigation_menu_recent;
    private final static int BOT_MENU_ITEM_LOCATION_ID = R.id.navigation_menu_location;
    private final static int BOT_MENU_ITEM_OPTIONS_ID = R.id.navigation_menu_options;

    int mCurrentFragmentId;

    FrameLayout mMainMenuLayout;

    BottomNavigationView mBottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.Companion.getAppComponent().inject(this);
        mMainMenuLayout = findViewById(R.id.main_menu_layout);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.Companion.disableShiftMode(mBottomNavigationView);
        if (savedInstanceState == null) {
            // by default, select mainMenuFragment
            mCurrentFragmentId = BOT_MENU_ITEM_DEFAULT_ID;
            onMenuItemSelected(mCurrentFragmentId);
        } else {
            // restore mainMenuFragment Instance
            //mCurrentFragmentId = savedInstanceState.getInt(CURRENT_FRAGMENT_ID);
            //mBottomNavigationView.setSelectedItemId(mCurrentFragmentId);
        }
    }

    /*
    @Override
    public void onBackPressed() {
        // test
        Logger.i("num of support fragments = " + getSupportFragmentManager().getBackStackEntryCount());
        Logger.i("num of fragments = " + getFragmentManager().getBackStackEntryCount());
    }
    */

    public void onMenuItemSelected(int itemId) {
        switch (itemId) {
            case BOT_MENU_ITEM_PROFILE_ID:
                showFragment(ProfileFragment.newInstance());
                break;
            case BOT_MENU_ITEM_RECENT_ID:
                showFragment(RecentFragment.newInstance());
                break;
            case BOT_MENU_ITEM_LOCATION_ID:
                showFragment(LocationFragment.newInstance());
                break;
            case BOT_MENU_ITEM_OPTIONS_ID:
                showFragment(OptionsFragment.newInstance());
                break;
            default:
                showFragment(RecentFragment.newInstance());
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(CURRENT_FRAGMENT_ID, mCurrentFragmentId);
    }

    public void showFragment(BaseFragment fragment) {
        fragment.showFragment(this, R.id.fragment_container, true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mCurrentFragmentId = item.getItemId();
        onMenuItemSelected(item.getItemId());
        return true;
    }

}
