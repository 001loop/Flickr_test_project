package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotosets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponsePhotosets (
        @SerializedName("photosets")        @Expose     var photosets: Photosets,
        // error body
        @SerializedName("stat")             @Expose     var stat: String,
        @SerializedName("code")             @Expose     var code: Int,
        @SerializedName("message")          @Expose     var message: String
)