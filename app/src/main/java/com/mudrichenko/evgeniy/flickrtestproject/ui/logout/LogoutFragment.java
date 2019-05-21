package com.mudrichenko.evgeniy.flickrtestproject.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment;
import com.mudrichenko.evgeniy.flickrtestproject.ui.launcher.LauncherActivity;
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils;

import javax.inject.Inject;

public class LogoutFragment extends BaseFragment implements LogoutView, View.OnClickListener{

    @InjectPresenter
    LogoutPresenter mLogoutPresenter;

    @Inject
    PrefUtils mPrefUtils;

    Button btnViewCancel;
    Button btnViewLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        App.Companion.getAppComponent().inject(this);
        btnViewCancel = view.findViewById(R.id.btnViewCancel);
        btnViewCancel.setOnClickListener(this);
        btnViewLogout = view.findViewById(R.id.btnViewLogout);
        btnViewLogout.setOnClickListener(this);
        return view;
    }

    public static LogoutFragment newInstance() {
        return new LogoutFragment();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnViewCancel:
                clickOnCancel();
                break;
            case R.id.btnViewLogout:
                clickOnLogout();
                break;
        }
    }

    private void clickOnCancel() {
        removeFragment();
    }

    private void clickOnLogout() {
        mPrefUtils.putAuthToken(null);
        startActivity(new Intent(getActivity(), LauncherActivity.class));
        removeFragment();
    }

    private void removeFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }
}
