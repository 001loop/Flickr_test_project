package com.mudrichenko.evgeniy.flickrtestproject.api

import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responseContactList.ResponseContactList
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responseDeletePhoto.ResponseDeletePhoto
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo.ResponsePhotoInfo
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoSizes.ResponsePhotoSizes
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotos.ResponsePhotos
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoset.ResponsePhotoset
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotosets.ResponsePhotosets
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface EndpointInterface {

    @GET
    fun requestPhotosObservable(@Url url: String): Observable<ResponsePhotos>

    // coroutines
    /*
    @GET
    suspend fun requestPhotoInfo(@Url url: String): Response<ResponsePhotoInfo>
    */

    @GET
    fun requestPhotoInfo(@Url url: String): Observable<ResponsePhotoInfo>

    @GET
    fun requestPhotosets(@Url url: String): Observable<ResponsePhotosets>

    @GET
    fun requestPhotoset(@Url url: String): Observable<ResponsePhotoset>

    @GET
    fun requestContactList(@Url url: String): Observable<ResponseContactList>

    @GET
    fun requestDeletePhoto(@Url url: String): Observable<ResponseDeletePhoto>

    @GET
    fun requestPhotoSizes(@Url url: String): Observable<ResponsePhotoSizes>

    @GET
    fun simpleRequest(@Url url: String): Observable<ResponseBody>

}