package com.mudrichenko.evgeniy.flickrtestproject.api.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Photo(
        @SerializedName("id")       @Expose     var id: Long,
        @SerializedName("owner")    @Expose     var owner: String?,
        @SerializedName("secret")   @Expose     var secret: String,
        @SerializedName("server")   @Expose     var server: Int,
        @SerializedName("farm")     @Expose     var farm: Int,
        @SerializedName("title")    @Expose     var title: String,
        @SerializedName("ispublic") @Expose     var ispublic: Int,
        @SerializedName("isfriend") @Expose     var isfriend: Int,
        @SerializedName("isfamily") @Expose     var isfamily: Int
)