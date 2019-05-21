package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotosets

import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.Description
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.Title
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Photoset (
        @SerializedName("id")                       @Expose     var id: Long,
        @SerializedName("primary")                  @Expose     var primary: Long,
        @SerializedName("secret")                   @Expose     var secret: String,
        @SerializedName("server")                   @Expose     var server: Int,
        @SerializedName("farm")                     @Expose     var farm: Int,
        @SerializedName("photos")                   @Expose     var photos: Int,
        @SerializedName("videos")                   @Expose     var videos: Int,
        @SerializedName("title")                    @Expose     var title: Title,
        @SerializedName("description")              @Expose     var description: Description,
        @SerializedName("needs_interstitial")       @Expose     var needsInterstitial: Int,
        @SerializedName("visibility_can_see_set")   @Expose     var visibilityCanSeeSet: Int,
        @SerializedName("count_views")              @Expose     var countViews: Int,
        @SerializedName("count_comments")           @Expose     var countComments: Int,
        @SerializedName("can_comment")              @Expose     var canComment: Int,
        @SerializedName("date_create")              @Expose     var dateCreate: Long,
        @SerializedName("date_update")              @Expose     var dateUpdate: Long
)