package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Owner (
        @SerializedName("nsid")             @Expose     var nsid: String,
        @SerializedName("username")         @Expose     var userName: String,
        @SerializedName("realname")         @Expose     var realName: String,
        @SerializedName("location")         @Expose     var location: String,
        @SerializedName("iconserver")       @Expose     var iconServer: String,
        @SerializedName("iconfarm")         @Expose     var iconFarm: Int,
        @SerializedName("path_alias")       @Expose     var pathAlias: Any
)