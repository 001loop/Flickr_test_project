package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responseContactList

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Contact (
        @SerializedName("nsid")             @Expose     var nsid: String,
        @SerializedName("username")         @Expose     var username: String,
        @SerializedName("iconserver")       @Expose     var iconServer: String,
        @SerializedName("iconfarm")         @Expose     var iconFarm: Int,
        @SerializedName("ignored")          @Expose     var ignored: Int,
        @SerializedName("rev_ignored")      @Expose     var revIgnored: Int,
        @SerializedName("realname")         @Expose     var realName: String,
        @SerializedName("friend")           @Expose     var friend: Int,
        @SerializedName("family")           @Expose     var family: Int,
        @SerializedName("path_alias")       @Expose     var pathAlias: Any,
        @SerializedName("location")         @Expose     var location: String
)
