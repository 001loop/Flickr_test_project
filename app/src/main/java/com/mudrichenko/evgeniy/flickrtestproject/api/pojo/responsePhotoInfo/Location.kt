package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Location (
    @SerializedName("latitude")         @Expose     var latitude: Double,
    @SerializedName("longitude")        @Expose     var longitude: Double,
    @SerializedName("accuracy")         @Expose     var accuracy: Int,
    @SerializedName("context")          @Expose     var context: String,
    @SerializedName("region")           @Expose     var region: Region,
    @SerializedName("country")          @Expose     var country: Country,
    @SerializedName("place_id")         @Expose     var placeId: String,
    @SerializedName("woeid")            @Expose     var woeid: String
)
