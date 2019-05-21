package com.mudrichenko.evgeniy.flickrtestproject.api.pojo


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Title (
        @SerializedName("_content")     @Expose     var content: String
)