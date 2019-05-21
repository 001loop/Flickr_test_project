package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponsePhotoInfo(
        @SerializedName("photo")        @Expose     var photo: Photo,
        // error body
        @SerializedName("stat")         @Expose     var stat: String,
        @SerializedName("code")         @Expose     var code: Int,
        @SerializedName("message")      @Expose     var message: String
)
