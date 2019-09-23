package com.mudrichenko.evgeniy.flickrtestproject.ui.auth;

import android.content.Intent;
import android.net.Uri;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mudrichenko.evgeniy.flickrtestproject.App;
import com.mudrichenko.evgeniy.flickrtestproject.api.ApiConstants;
import com.mudrichenko.evgeniy.flickrtestproject.api.EndpointInterface;
import com.mudrichenko.evgeniy.flickrtestproject.data.repository.AuthRepository;
import com.mudrichenko.evgeniy.flickrtestproject.R;
import com.mudrichenko.evgeniy.flickrtestproject.utils.AuthUtils;
import com.mudrichenko.evgeniy.flickrtestproject.utils.NetworkUtils;
import com.mudrichenko.evgeniy.flickrtestproject.utils.PrefUtils;
import com.mudrichenko.evgeniy.flickrtestproject.utils.StringUtils;

import javax.inject.Inject;

@InjectViewState
public class AuthPresenter extends MvpPresenter<AuthView> implements AuthRepository.AuthRepositoryListener {

    @Inject
    EndpointInterface mEndpointInterface;

    @Inject
    NetworkUtils mNetworkUtils;

    @Inject
    AuthUtils mAuthUtils;

    @Inject
    PrefUtils mPrefUtils;

    @Inject
    StringUtils mStringUtils;

    @Inject
    AuthRepository mAuthRepository;

    public AuthPresenter() {
        App.Companion.getAppComponent().inject(this);
    }

    public void clickOnLogin() {
        if (mNetworkUtils.isInternetConnectionAvailable()) {
            loginToRest();
        } else {
            getViewState().showMessageNoInternetConnection();
        }
    }

    private void loginToRest() {
        getViewState().startLogin();
        mAuthRepository.setAuthRepositoryListener(this);
        mAuthRepository.onRequestToken(this);
    }

    public void onResume(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            mAuthRepository.onRequestAccessToken(this,
                    uri.getQueryParameter("oauth_verifier"), uri.getQueryParameter("oauth_token"));
        }
    }

    public void onViewDestroy() {
        mAuthRepository.setAuthRepositoryListener(null);
    }

    @Override
    public void onAuthTokenReceived(String responseBodyString) {
        String authToken = mAuthUtils.getAuthTokenFromResponse(responseBodyString);
        String authTokenSecret = mAuthUtils.getAuthTokenSecretFromResponse(responseBodyString);
        mPrefUtils.putAuthToken(authToken);
        mPrefUtils.putAuthTokenSecret(authTokenSecret);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAuthUtils.getUserAuthLink(authToken)));
        getViewState().startOauthIntent(intent);
    }

    @Override
    public void onAccessTokenReceived(String responseString) {
        mPrefUtils.putUserFullname(
                mStringUtils.getParameterFromResponseString(responseString, ApiConstants.RESPONSE_FULL_NAME));
        mPrefUtils.putUserId(
                mStringUtils.getParameterFromResponseString(responseString, ApiConstants.RESPONSE_USER_NSID));
        mPrefUtils.putUserName(
                mStringUtils.getParameterFromResponseString(responseString, ApiConstants.RESPONSE_USER_NAME));
        mPrefUtils.putAuthToken(
                mStringUtils.getParameterFromResponseString(responseString, ApiConstants.RESPONSE_AUTH_TOKEN));
        mPrefUtils.putAuthTokenSecret(
                mStringUtils.getParameterFromResponseString(responseString, ApiConstants.RESPONSE_AUTH_TOKEN_SECRET));
        getViewState().loginSuccess();
    }

    @Override
    public void onErrorReceived(String errorMessage) {
        getViewState().showErrorMessage(errorMessage);
    }

}
