package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Editability (
    @SerializedName("canComment")       @Expose     var canComment: Int,
    @SerializedName("canaddmeta")       @Expose     var canAddMeta: Int
)