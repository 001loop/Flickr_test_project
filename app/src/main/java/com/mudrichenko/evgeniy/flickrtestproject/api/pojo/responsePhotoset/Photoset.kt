package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoset

import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.Photo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Photoset (
        @SerializedName("id")           @Expose     var id: String,
        @SerializedName("primary")      @Expose     var primary: String,
        @SerializedName("owner")        @Expose     var owner: String,
        @SerializedName("ownername")    @Expose     var ownerName: String,
        @SerializedName("photo")        @Expose     var photo: List<Photo>,
        @SerializedName("page")         @Expose     var page: Int,
        @SerializedName("per_page")     @Expose     var perPage: Int,
        @SerializedName("pages")        @Expose     var pages: Int,
        @SerializedName("title")        @Expose     var title: String,
        @SerializedName("total")        @Expose     var total: Int
)