package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responseContactList

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponseContactList (
        @SerializedName("contacts")         @Expose     var contacts: Contacts,
        // error body
        @SerializedName("stat")             @Expose     var stat: String,
        @SerializedName("code")             @Expose     var code: Int,
        @SerializedName("message")          @Expose     var message: String
)
