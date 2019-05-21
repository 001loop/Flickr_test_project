package com.mudrichenko.evgeniy.flickrtestproject.ui.location

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem

@StateStrategyType(AddToEndSingleStrategy::class)
interface LocationView : MvpView {

    fun showProgressWheel()

    fun hideProgressWheel()

    fun showInfoMessage(message: String)

    fun hideInfoMessage()

    fun showPhotos(recyclerViewItems: List<RecyclerViewItem>)

    fun resetPhotoList()

    @StateStrategyType(SkipStrategy::class)
    fun showPhotoFullscreenFragment(index: Int)

    fun lastPageReached()

    fun setLocationName(locationName: String)

}
