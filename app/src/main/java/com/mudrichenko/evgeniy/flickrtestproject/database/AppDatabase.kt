package com.mudrichenko.evgeniy.flickrtestproject.database


import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.mudrichenko.evgeniy.flickrtestproject.data.converter.DbConverter

@Database(entities = [FlickrPhoto::class, FlickrContact::class, FlickrPhotoset::class], version = 1)
@TypeConverters(DbConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun flickrPhotoDao(): FlickrPhotoDao

    abstract fun flickrContactDao(): FlickrContactDao

    abstract fun flickrPhotosetDao(): FlickrPhotosetDao

}
