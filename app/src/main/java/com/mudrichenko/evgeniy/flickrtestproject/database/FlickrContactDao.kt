package com.mudrichenko.evgeniy.flickrtestproject.database

import androidx.room.*
import io.reactivex.Single

@Dao
interface FlickrContactDao {

    @Query("SELECT * FROM flickrcontact")
    fun getAll(): Single<List<FlickrContact>>

    @Query("SELECT * FROM flickrcontact WHERE nsid = :nsid")
    fun getById(nsid: String): FlickrContact

    @Query("SELECT * FROM flickrcontact WHERE friend = :friend")
    fun getFriends(friend: Boolean): List<FlickrContact>

    @Query("SELECT * FROM flickrcontact WHERE family = :family")
    fun getFamily(family: Boolean): List<FlickrContact>

    @Query("SELECT * FROM flickrcontact WHERE ignored = :ignored")
    fun getIgnored(ignored: Boolean): List<FlickrContact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(flickrcontacts: List<FlickrContact>)

    @Query("DELETE FROM flickrcontact")
    fun removeAll()

    @Update
    fun update(flickrcontact: FlickrContact)

    @Delete
    fun delete(flickrcontact: FlickrContact)

}