package com.mudrichenko.evgeniy.flickrtestproject.ui.options;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.ui.aboutApp.AboutAppFragment;
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment;
import com.mudrichenko.evgeniy.flickrtestproject.ui.logout.LogoutFragment;

import java.util.LinkedList;
import java.util.Scanner;

public class OptionsFragment extends BaseFragment implements OptionsView,
        View.OnClickListener {

    @InjectPresenter
    OptionsPresenter mOptionsPresenter;

    private OptionsListener mOptionsListener;

    Button btnLogout;
    Button btnClearDb;
    Button btnPrintDb;
    Button btnAboutApp;
    Button btnCheckToken;
    //Button btnFirebaseSign;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options, container, false);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);
        btnClearDb = view.findViewById(R.id.btn_clear_db);
        btnClearDb.setOnClickListener(this);
        btnPrintDb = view.findViewById(R.id.btn_print_db);
        btnPrintDb.setOnClickListener(this);
        btnAboutApp = view.findViewById(R.id.btn_about_app);
        btnAboutApp.setOnClickListener(this);
        btnCheckToken = view.findViewById(R.id.btn_check_token);
        btnCheckToken.setOnClickListener(this);
        //btnFirebaseSign = view.findViewById(R.id.btn_firebase_sign);
        //btnFirebaseSign.setOnClickListener(this);
        return view;
    }

    public static OptionsFragment newInstance() {
        return new OptionsFragment();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_logout:
                clickOnLogout();
                break;
            case R.id.btn_check_token:
                clickOnCheckToken();
                break;
            case R.id.btn_clear_db:
                clickOnClearDB();
                break;
            case R.id.btn_print_db:
                clickOnPrintDb();
                break;
            case R.id.btn_about_app:
                clickOnAboutApp();
                break;
                /*
            case R.id.btn_firebase_sign:
                clickOnFirebaseSign();
                break;
                */
        }
    }

    private void clickOnCheckToken() {
        mOptionsPresenter.checkToken();
    }

    private void clickOnLogout() {
        showLogoutFragment();
    }

    private void clickOnClearDB() {
        mOptionsPresenter.clearDb();
    }

    private void clickOnPrintDb() {
        mOptionsPresenter.printDb();
    }

    private void clickOnAboutApp() {
        showAboutAppFragment();
    }

    private void clickOnFirebaseSign() {
        if (mOptionsListener != null) {
            mOptionsListener.firebaseSign();
        }
    }

    @Override
    public void showAboutAppFragment() {
        AboutAppFragment.newInstance().showFragment(getActivity(), R.id.fullscreen_fragment_container, false);
    }

    @Override
    public void showLogoutFragment() {
        LogoutFragment.Companion.newInstance().showFragment(getActivity(), R.id.fullscreen_fragment_container, false);
    }

    @Override
    public void changeFirebaseBtnText(boolean isSignedIn) {
        /*
        if (isSignedIn) {
            btnFirebaseSign.setText(getResources().getString(R.string.firebase_sign_out));
        } else {
            btnFirebaseSign.setText(getResources().getString(R.string.firebase_sign_in));
        }
        */
    }

    public void setOptionsListener(OptionsListener optionsListener) {
        mOptionsListener = optionsListener;
    }

    public interface OptionsListener {
        void firebaseSign();
    }

}
