package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Dates (
        @SerializedName("posted")           @Expose     var posted: String,
        @SerializedName("taken")            @Expose     var taken: String,
        @SerializedName("takengranularity") @Expose     var takenGranularity: String,
        @SerializedName("takenunknown")     @Expose     var takenUnknown: String,
        @SerializedName("lastupdate")       @Expose     var lastUpdate: String? = null
)