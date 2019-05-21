package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Tag (
        @SerializedName("id")           @Expose     var id: String,
        @SerializedName("author")       @Expose     var author: String,
        @SerializedName("authorname")   @Expose     var authorName: String,
        @SerializedName("raw")          @Expose     var raw: String,
        @SerializedName("_content")     @Expose     var content: String
)