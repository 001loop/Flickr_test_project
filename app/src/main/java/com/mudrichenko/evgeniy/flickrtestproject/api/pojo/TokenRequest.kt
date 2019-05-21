package com.mudrichenko.evgeniy.flickrtestproject.api.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TokenRequest(
        @SerializedName("oauth_nonce")              @Expose     var oauthNonce: String,
        @SerializedName("oauth_timestamp")          @Expose     var oauthTimestamp: Long,
        @SerializedName("oauth_consumer_key")       @Expose     var oauthConsumerKey: String,
        @SerializedName("oauth_signature_method")   @Expose     var oauthSignatureMethod: String,
        @SerializedName("oauth_version")            @Expose     var oauthVersion: String,
        @SerializedName("oauth_callback")           @Expose     var oauthCallback: String,
        @SerializedName("oauth_signature")          @Expose     var oauthSignature: String
)