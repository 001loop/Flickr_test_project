package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponsePhotos(
        @SerializedName("photos")   @Expose     val photos: Photos,
        @SerializedName("stat")     @Expose     val stat: String,
        @SerializedName("code")     @Expose     val code: Int,
        @SerializedName("message")  @Expose     val message: String
)