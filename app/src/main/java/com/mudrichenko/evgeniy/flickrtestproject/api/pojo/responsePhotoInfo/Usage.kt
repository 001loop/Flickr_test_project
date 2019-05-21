package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Usage (
        @SerializedName("candownload")          @Expose     var canDownload: Int,
        @SerializedName("canblog")              @Expose     var canBlog: Int,
        @SerializedName("canprint")             @Expose     var canPrint: Int,
        @SerializedName("canshare")             @Expose     var canShare: Int
)