package com.mudrichenko.evgeniy.flickrtestproject.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.mudrichenko.evgeniy.flickrtestproject.data.converter.DbConverter
import java.io.Serializable

/*
@Entity
data class FlickrPhoto (
        // todo save all photo info. set null if no info

        @PrimaryKey(autoGenerate = true)
        val id: Long,                           // DB id
        val flickrId: Long,                     // info from photos array
        val type: Int,                          // info from photos repository, given by app
        val page: Int,                          // info from photos repository, given by app
        val name: String,                       // info from photos array (title)
        val url: String,                        // info from photos array
        var urlOriginalSize: String?,           // info from photo sizes
        val ownerId: String,                      // info from photos array
        val isPublic: Boolean,                  // info from photos array
        val isFriend: Boolean,                  // info from photos array
        val isFamily: Boolean,                  // info from photos array

        val album: Long                         // info from albums repository. We need this data only for albums fragment

): Serializable
*/


// todo test
// @Entity
@Entity(indices = [Index(value = ["flickrId"], unique = true)])
data class FlickrPhoto (
        // todo save all photo info. set null if no info

        @PrimaryKey(autoGenerate = true)
        var id: Long,                           // DB id
        var flickrId: Long,                     // info from photos array
        var type: Int,                          // info from photos repository, given by app
        var page: Int,                          // info from photos repository, given by app
        var name: String,                       // info from photos array (title)
        var url: String,                        // info from photos array
        var ownerId: String?,                    // info from photos array
        var isPublic: Boolean,                  // info from photos array
        var isFriend: Boolean,                  // info from photos array
        var isFamily: Boolean,                  // info from photos array

        var album: Long,                        // info from albums repository. We need this data only for albums fragment

        var urlOriginalSize: String?,           // info from photo sizes

        var urlPhotoPage: String?,               // info from photo info
        var description: String?,                // info from photo info
        var dateTaken: String?,                  // info from photo info
        var numOfViews: Int,                    // info from photo info
        var ownerName: String?,                  // info from photo info
        var ownerRealName: String?,              // info from photo info
        @TypeConverters(DbConverter::class)
        var tagIds: ArrayList<String>?,               // info from photo info
        @TypeConverters(DbConverter::class)
        var tagRaw: ArrayList<String>?,               // info from photo info
        var safetyLevel: Int,                   // info from photo info

        var locationLat: Double,                // info from photo location
        var locationLng: Double,                // info from photo location
        var locationAccuracy: Int               // info from photo location


): Serializable