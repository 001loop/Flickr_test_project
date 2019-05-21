package com.mudrichenko.evgeniy.flickrtestproject.ui.contactList

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.mudrichenko.evgeniy.flickrtestproject.data.model.RecyclerViewItem

import java.util.ArrayList

@StateStrategyType(AddToEndStrategy::class)
interface ContactListView : MvpView {

    fun showProgressWheel()

    fun hideProgressWheel()

    fun showInfoMessage(message: String)

    fun hideInfoMessage()

    fun showPhotos(recyclerViewItems: List<RecyclerViewItem>)

    fun resetPhotoList()

    @StateStrategyType(SkipStrategy::class)
    fun showPhotoFullscreenFragment(index: Int)

    fun lastPageReached()

    fun onContactListLoaded(contacts: ArrayList<String>)

}
