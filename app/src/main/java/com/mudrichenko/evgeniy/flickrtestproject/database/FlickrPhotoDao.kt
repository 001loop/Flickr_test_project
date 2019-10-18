package com.mudrichenko.evgeniy.flickrtestproject.database

import androidx.room.*
import io.reactivex.Single

@Dao
interface FlickrPhotoDao {

    @Query("SELECT * FROM flickrphoto")
    fun getAll(): List<FlickrPhoto>

    @Query("SELECT * FROM flickrphoto WHERE id = :id")
    fun getById(id: Long): FlickrPhoto

    @Query("SELECT COUNT(*) FROM flickrphoto WHERE id = :id")
    fun isPhotoExist(id: Long): Int

    @Query("SELECT * FROM flickrphoto WHERE flickrId = :flickrId AND type = :type")
    fun getByIdAndType(flickrId: Long, type: Int): FlickrPhoto

    @Query("SELECT * FROM flickrphoto WHERE flickrId = :flickrId")
    fun getByFlickrId(flickrId: Long): Single<FlickrPhoto>

    @Query("SELECT * FROM flickrphoto WHERE type = :type")
    fun getByType(type: Int): List<FlickrPhoto>

    @Query("SELECT * FROM flickrphoto WHERE type = :type AND page = :page")
    fun getByTypeAndPage(type: Int, page: Int): List<FlickrPhoto>

    @Query("SELECT * FROM flickrphoto WHERE type = :type AND page = :page")
    fun getPhotosByTypeAndPage(type: Int, page: Int): Single<List<FlickrPhoto>>

    @Query("SELECT * FROM flickrphoto WHERE type = :type AND page = :page And ownerId = :ownerId")
    fun getPhotosByOwnerAndPage(type: Int, page: Int, ownerId: String): Single<List<FlickrPhoto>>

    @Query("SELECT * FROM flickrphoto WHERE type = :type AND page = :page And album = :album")
    fun getPhotosByAlbumAndPage(type: Int, page: Int, album: Long): Single<List<FlickrPhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhoto(flickrPhoto: FlickrPhoto)

    // coroutines
    /*
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(flickrPhoto: FlickrPhoto)
    */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotos(flickrPhotos: List<FlickrPhoto>)

    @Query("DELETE FROM flickrphoto WHERE type = :type")
    fun removeAllByType(type: Int)

    @Query("DELETE FROM flickrphoto WHERE type = :type")
    fun removeAllPhotosByType(type: Int)

    @Query("DELETE FROM flickrphoto WHERE type = :type And ownerId = :ownerId")
    fun removeAllPhotosByTypeAndOwner(type: Int, ownerId: String)

    @Query("DELETE FROM flickrphoto WHERE type = :type And album = :album")
    fun removeAllPhotosByTypeAndAlbum(type: Int, album: Long)

    @Query("DELETE FROM flickrphoto WHERE flickrId = :flickrId")
    fun removeByFlickrId(flickrId: Long)

    @Query("DELETE FROM flickrphoto")
    fun removeAll()

    @Update
    fun update(flickrPhoto: FlickrPhoto)

    @Delete
    fun delete(flickrPhoto: FlickrPhoto)

}