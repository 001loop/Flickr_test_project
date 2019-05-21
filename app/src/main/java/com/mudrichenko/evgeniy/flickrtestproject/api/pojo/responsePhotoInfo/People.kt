package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class People (
    @SerializedName("haspeople")        @Expose     var hasPeople: Int? = null
)
