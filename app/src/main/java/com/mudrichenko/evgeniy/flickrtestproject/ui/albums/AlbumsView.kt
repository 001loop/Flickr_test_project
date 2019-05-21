package com.mudrichenko.evgeniy.flickrtestproject.ui.albums

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem

import java.util.ArrayList

@StateStrategyType(AddToEndStrategy::class)
interface AlbumsView : MvpView {

    fun showProgressWheel()

    fun hideProgressWheel()

    fun showInfoMessage(message: String)

    fun hideInfoMessage()

    fun showPhotos(recyclerViewItems: List<RecyclerViewItem>)

    fun resetPhotoList()

    @StateStrategyType(SkipStrategy::class)
    fun showPhotoFullscreenFragment(index: Int)

    fun lastPageReached()

    fun onPhotosetListLoaded(mPhotosetsTitles: ArrayList<String>)


    fun showAlbumsSpinner()

    fun hideAlbumsSpinner()

    fun selectSpinnerItem(itemId: Int)

}
