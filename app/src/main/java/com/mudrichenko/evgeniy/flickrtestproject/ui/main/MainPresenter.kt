package com.mudrichenko.evgeniy.flickrtestproject.ui.main

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.R

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    override fun onFirstViewAttach() {
        viewState.onMenuItemSelected(0)
    }

    init {
        App.appComponent!!.inject(this)
    }

    fun onMenuItemSelected(itemId: Int) {
        viewState.onMenuItemSelected(itemId)
    }

}