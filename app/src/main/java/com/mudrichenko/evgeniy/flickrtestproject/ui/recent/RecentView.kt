package com.mudrichenko.evgeniy.flickrtestproject.ui.recent

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem

@StateStrategyType(AddToEndStrategy::class)
interface RecentView : MvpView {

    fun showProgressWheel()

    fun hideProgressWheel()

    fun showRefreshWheel()

    fun hideRefreshWheel()

    fun showInfoMessage(message: String)

    @StateStrategyType(SkipStrategy::class)
    fun showSnackbarMessage(message: String)

    fun hideInfoMessage()

    fun showPhotos(recyclerViewItems: List<RecyclerViewItem>)

    fun resetPhotoList()

    @StateStrategyType(SkipStrategy::class)
    fun showPhotoFullscreenFragment(index: Int)

    fun lastPageReached()

}
