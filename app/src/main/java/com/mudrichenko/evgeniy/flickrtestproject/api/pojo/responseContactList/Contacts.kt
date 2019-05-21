package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responseContactList

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Contacts (
        @SerializedName("page")         @Expose     var page: Int,
        @SerializedName("pages")        @Expose     var pages: Int,
        @SerializedName("per_page")     @Expose     var perPage: Int,
        @SerializedName("total")        @Expose     var total: Int,
        @SerializedName("contact")      @Expose     var contact: List<Contact>
)