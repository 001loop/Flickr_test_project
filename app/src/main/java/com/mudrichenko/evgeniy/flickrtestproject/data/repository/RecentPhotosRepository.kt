package com.mudrichenko.evgeniy.flickrtestproject.data.repository

import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.FlickrPhotosTypes

class RecentPhotosRepository : BasePhotosRepository() {

    override val FLICKR_PHOTO_TYPE = FlickrPhotosTypes.TYPE_RECENT

    init {
        App.appComponent!!.inject(this)
    }

}