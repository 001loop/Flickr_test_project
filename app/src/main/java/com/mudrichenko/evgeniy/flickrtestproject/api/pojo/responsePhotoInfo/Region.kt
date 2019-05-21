package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Region (
        @SerializedName("_content")         @Expose     var content: String,
        @SerializedName("place_id")         @Expose     var placeId: String,
        @SerializedName("woeid")            @Expose     var woeid: String
)