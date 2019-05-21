package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotosets

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Photosets (
        @SerializedName("cancreate")        @Expose     var canCreate: Int,
        @SerializedName("page")             @Expose     var page: Int,
        @SerializedName("pages")            @Expose     var pages: Int,
        @SerializedName("per_page")         @Expose     var perPage: Int,
        @SerializedName("total")            @Expose     var total: Int,
        @SerializedName("photoset")         @Expose     var photoset: List<Photoset>
)