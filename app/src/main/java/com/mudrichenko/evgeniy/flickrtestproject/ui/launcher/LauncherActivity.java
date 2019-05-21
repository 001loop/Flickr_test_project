package com.mudrichenko.evgeniy.flickrtestproject.ui.launcher;

import android.content.Intent;
import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.ui.auth.AuthActivity;
import com.mudrichenko.evgeniy.flickrtestproject.ui.main.MainActivity;
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

public class LauncherActivity extends MvpAppCompatActivity implements LauncherView {

    @InjectPresenter
    LauncherPresenter mLauncherPresenter;

    @Inject
    PrefUtils mPrefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        //
        App.Companion.getAppComponent().inject(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
        boolean isAuthTokenCorrect = true;
        String authToken = mPrefUtils.getAuthToken();
        Logger.i("authToken null = " + (authToken==null));
        Logger.i("authToken = " + authToken);

        if (authToken == null) {
            isAuthTokenCorrect = false;
        } else if (authToken.equals("")) {
            isAuthTokenCorrect = false;
        }
        if (!isAuthTokenCorrect) {
            startAuthActivity();
        } else {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void startAuthActivity() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

}
