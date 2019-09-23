package com.mudrichenko.evgeniy.flickrtestproject.database

import androidx.room.*
import io.reactivex.Single

@Dao
interface  FlickrPhotosetDao {

    @Query("SELECT * FROM flickrphotoset")
    fun getAll(): Single<List<FlickrPhotoset>>

    @Query("SELECT * FROM flickrphotoset WHERE id = :id")
    fun getById(id: Long): FlickrPhotoset

    @Query("SELECT * FROM flickrphotoset WHERE ownerId = :ownerId")
    fun getByOwnerId(ownerId: Long): List<FlickrPhotoset>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(flickrphotoset: FlickrPhotoset)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(flickrphotosets: List<FlickrPhotoset>)

    @Query("DELETE FROM flickrphotoset")
    fun removeAll(): Int

    @Query("DELETE FROM flickrphotoset WHERE id = :id")
    fun removeById(id: Long)

    @Update
    fun update(flickrphotoset: FlickrPhotoset)

    @Delete
    fun delete(flickrphotoset: FlickrPhotoset)

}