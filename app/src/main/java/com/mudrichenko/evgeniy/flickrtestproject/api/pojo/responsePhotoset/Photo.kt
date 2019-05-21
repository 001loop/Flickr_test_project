package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoset

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Photo (
        @SerializedName("id")           @Expose     var id: Long,
        @SerializedName("secret")       @Expose     var secret: String,
        @SerializedName("server")       @Expose     var server: Int,
        @SerializedName("farm")         @Expose     var farm: Int,
        @SerializedName("title")        @Expose     var title: String,
        @SerializedName("isprimary")    @Expose     var isPrimary: String,
        @SerializedName("ispublic")     @Expose     var isPublic: Int,
        @SerializedName("isfriend")     @Expose     var isFriend: Int,
        @SerializedName("isfamily")     @Expose     var isFamily: Int
)