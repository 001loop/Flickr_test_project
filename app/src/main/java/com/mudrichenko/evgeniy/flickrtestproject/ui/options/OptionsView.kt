package com.mudrichenko.evgeniy.flickrtestproject.ui.options

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndStrategy::class)
interface OptionsView : MvpView {

    fun showAboutAppFragment()

    fun showLogoutFragment()

}
