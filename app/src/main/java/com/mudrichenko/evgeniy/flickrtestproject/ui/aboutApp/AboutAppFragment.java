package com.mudrichenko.evgeniy.flickrtestproject.ui.aboutApp;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.ui.BaseFragment;

public class AboutAppFragment extends BaseFragment implements AboutAppView, View.OnClickListener{

    @InjectPresenter
    AboutAppPresenter mAboutAppPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_app, container, false);
        ImageView btnViewClose = view.findViewById(R.id.btnViewClose);
        btnViewClose.setOnClickListener(this);
        Button btnViewFeedback = view.findViewById(R.id.btnFeedback);
        btnViewFeedback.setOnClickListener(this);
        TextView textViewVersion = view.findViewById(R.id.textViewViewsAppVersionDescription);
        textViewVersion.setText(mAboutAppPresenter.getAppVersion(getContext()));
        return view;
    }

    public static AboutAppFragment newInstance() {
        return new AboutAppFragment();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnViewClose:
                clickOnClose();
                break;
            case R.id.btnFeedback:
                clickOnFeedback();
                break;
        }
    }

    private void clickOnClose() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }

    private void clickOnFeedback() {
        mAboutAppPresenter.createMailIntent(getContext());
    }

    @Override
    public void startActivityFromIntent(Intent intent) {
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

}
