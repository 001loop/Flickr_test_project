package com.mudrichenko.evgeniy.flickrtestproject.api.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TokenResponse(
        @SerializedName("oauth_callback_confirmed")     @Expose var isCallbackConfirmed: Boolean,
        @SerializedName("oauth_token")                  @Expose var oauthToken: String,
        @SerializedName("oauth_token_secret")           @Expose var oauthTokenSecret: String
)