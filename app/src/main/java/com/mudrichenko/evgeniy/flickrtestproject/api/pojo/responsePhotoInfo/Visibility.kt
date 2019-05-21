package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Visibility (
        @SerializedName("ispublic")         @Expose     var isPublic: Int,
        @SerializedName("isfriend")         @Expose     var isFriend: Int,
        @SerializedName("isfamily")         @Expose     var isFamily: Int? = null
)