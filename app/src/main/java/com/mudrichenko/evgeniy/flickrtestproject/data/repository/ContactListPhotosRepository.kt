package com.mudrichenko.evgeniy.flickrtestproject.data.repository

import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.FlickrPhotosTypes

class ContactListPhotosRepository : BasePhotosRepository() {

    override val FLICKR_PHOTO_TYPE = FlickrPhotosTypes.TYPE_CONTACT_LIST_PHOTOS

    init {
        App.appComponent!!.inject(this)
    }

}