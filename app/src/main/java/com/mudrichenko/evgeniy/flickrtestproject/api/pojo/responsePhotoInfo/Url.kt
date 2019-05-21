package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Url (
        @SerializedName("type")         @Expose     var type: String,
        @SerializedName("_content")     @Expose     var content: String
)