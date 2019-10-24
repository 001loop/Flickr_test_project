package com.mudrichenko.evgeniy.flickrtestproject.ui.main

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface MainView : MvpView {

    fun onMenuItemSelected(itemId: Int)

}