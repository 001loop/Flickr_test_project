package com.mudrichenko.evgeniy.flickrtestproject.ui.auth;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.ui.main.MainActivity;
import com.todddavies.components.progressbar.ProgressWheel;

import org.jetbrains.annotations.NotNull;

public class AuthActivity extends MvpAppCompatActivity implements AuthView, View.OnClickListener{

    @InjectPresenter
    AuthPresenter mAuthPresenter;

    Button mBtnLogin;

    TextView mTextViewInfo;

    ProgressWheel mProgressWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(this);
        mTextViewInfo = findViewById(R.id.textViewInfo);
        mProgressWheel = findViewById(R.id.progress_wheel);
    }

    private void clickOnLogin() {
        mAuthPresenter.clickOnLogin();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                clickOnLogin();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuthPresenter.onResume(getIntent());
    }

    protected void onDestroy() {
        super.onDestroy();
        mAuthPresenter.onViewDestroy();
    }

    @Override
    public void startOauthIntent(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void loginSuccess() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void showMessageNoInternetConnection() {
        Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage(String message) {
        mTextViewInfo.setVisibility(View.VISIBLE);
        mTextViewInfo.setText(message);
    }

    @Override
    public void showProgressBar(@NotNull String message) {
        mProgressWheel.setVisibility(View.VISIBLE);
        mTextViewInfo.setVisibility(View.VISIBLE);
        mTextViewInfo.setText(message);
    }

    @Override
    public void hideProgressBar() {
        mProgressWheel.setVisibility(View.INVISIBLE);
        mTextViewInfo.setVisibility(View.INVISIBLE);
    }

}
