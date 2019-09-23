package com.mudrichenko.evgeniy.flickrtestproject.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mudrichenko.evgeniy.flickrtestproject.data.converter.DbConverter

@Database(entities = [FlickrPhoto::class, FlickrContact::class, FlickrPhotoset::class], version = 1)
@TypeConverters(DbConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun flickrPhotoDao(): FlickrPhotoDao

    abstract fun flickrContactDao(): FlickrContactDao

    abstract fun flickrPhotosetDao(): FlickrPhotosetDao

}
