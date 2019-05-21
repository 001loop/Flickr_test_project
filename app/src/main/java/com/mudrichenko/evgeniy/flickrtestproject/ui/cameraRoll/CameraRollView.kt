package com.mudrichenko.evgeniy.flickrtestproject.ui.cameraRoll

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.*
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem

@StateStrategyType(AddToEndStrategy::class)
interface CameraRollView : MvpView {

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
