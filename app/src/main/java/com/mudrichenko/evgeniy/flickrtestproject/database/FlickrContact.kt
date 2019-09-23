package com.mudrichenko.evgeniy.flickrtestproject.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FlickrContact (
        @PrimaryKey
        val nsid: String,
        val username: String,
        val realname: String,
        val friend: Boolean,
        val family: Boolean,
        val ignored: Boolean
)