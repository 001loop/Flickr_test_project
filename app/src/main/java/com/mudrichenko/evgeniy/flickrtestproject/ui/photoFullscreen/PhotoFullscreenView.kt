package com.mudrichenko.evgeniy.flickrtestproject.ui.photoFullscreen

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.mudrichenko.evgeniy.flickrtestproject.database.FlickrPhoto

@StateStrategyType(AddToEndStrategy::class)
interface PhotoFullscreenView : MvpView {

    fun showPhoto(flickrPhoto: FlickrPhoto, imageUrl: String)

    fun startLoadingPhoto()

}
