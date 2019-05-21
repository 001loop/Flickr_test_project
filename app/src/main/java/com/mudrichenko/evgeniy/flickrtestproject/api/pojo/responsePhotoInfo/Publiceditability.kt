package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Publiceditability (
        @SerializedName("cancomment")       @Expose     var canComment: Int,
        @SerializedName("canaddmeta")       @Expose     var canAddMeta: Int
)