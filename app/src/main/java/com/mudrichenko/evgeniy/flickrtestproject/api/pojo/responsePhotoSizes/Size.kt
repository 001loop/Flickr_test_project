package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoSizes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Size (
        @SerializedName("label")        @Expose     var label: String,
        @SerializedName("width")        @Expose     var width: String,
        @SerializedName("height")       @Expose     var height: String,
        @SerializedName("source")       @Expose     var source: String,
        @SerializedName("url")          @Expose     var url: String,
        @SerializedName("media")        @Expose     var media: String
)