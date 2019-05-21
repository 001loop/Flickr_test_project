package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Comments (
    @SerializedName("_content")         @Expose     var content: String
)