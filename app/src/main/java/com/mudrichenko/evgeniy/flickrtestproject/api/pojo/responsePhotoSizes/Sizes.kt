package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoSizes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Sizes (
        @SerializedName("canblog")          @Expose     var canBlog: Int,
        @SerializedName("canprint")         @Expose     var canPrint: Int,
        @SerializedName("candownload")      @Expose     var canDownload: Int,
        @SerializedName("size")             @Expose     var size: List<Size>
)