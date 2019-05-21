package com.mudrichenko.evgeniy.flickrtestproject.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class FlickrPhotoset (
        @PrimaryKey
        val id: Long,
        val ownerId: String,
        val photos: Int,
        val videos: Int,
        val countViews: Int,
        val dateCreate: Long,
        val dateUpdate: Long,
        val title: String,
        val description: String
)