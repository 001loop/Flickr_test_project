package com.mudrichenko.evgeniy.flickrtestproject.ui.auth

import android.content.Intent

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndStrategy::class)
interface AuthView : MvpView {

    fun startOauthIntent(intent: Intent)

    fun loginSuccess()

    fun showMessageNoInternetConnection()

    fun showErrorMessage(message: String)

    fun showProgressBar(message: String)

    fun hideProgressBar()

}
