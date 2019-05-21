package com.mudrichenko.evgeniy.flickrtestproject.data.repository

import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.FlickrPhotosTypes

class CameraRollPhotosRepository : BasePhotosRepository() {

    override val FLICKR_PHOTO_TYPE = FlickrPhotosTypes.TYPE_CAMERA_ROLL

    init {
        App.appComponent!!.inject(this)
    }

}