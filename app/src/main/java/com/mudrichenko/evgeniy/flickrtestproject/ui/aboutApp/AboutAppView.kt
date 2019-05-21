package com.mudrichenko.evgeniy.flickrtestproject.ui.aboutApp

import android.content.Intent

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndStrategy::class)
interface AboutAppView : MvpView {

    fun startActivityFromIntent(intent: Intent)

}
