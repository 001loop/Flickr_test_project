package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotos

import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.Photo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Photos (
        @SerializedName("page")     @Expose     val page: Int,
        @SerializedName("pages")    @Expose     val pages: Int,
        @SerializedName("per_page")  @Expose     val perPage: Int,
        @SerializedName("total")    @Expose     val total: Int,
        @SerializedName("photo")    @Expose     val photo: List<Photo>
)
