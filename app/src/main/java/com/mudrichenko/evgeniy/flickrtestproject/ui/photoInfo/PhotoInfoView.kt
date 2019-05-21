package com.mudrichenko.evgeniy.flickrtestproject.ui.photoInfo

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto

@StateStrategyType(OneExecutionStateStrategy::class)
interface PhotoInfoView : MvpView {

    fun showPhotoInfo(flickrPhoto: FlickrPhoto)

    fun showProgressWheel()

    fun hideProgressWheel()

    fun showInfoMessage(message: String)

    fun hideInfoMessage()

    fun onPhotoDeleted()

}
