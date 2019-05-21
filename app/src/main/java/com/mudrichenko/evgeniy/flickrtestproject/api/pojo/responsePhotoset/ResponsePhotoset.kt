package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoset

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponsePhotoset (
        @SerializedName("photoset")         @Expose     var photoset: Photoset,
        // error body
        @SerializedName("stat")             @Expose     var stat: String,
        @SerializedName("code")             @Expose     var code: Int,
        @SerializedName("message")          @Expose     var message: String
)
