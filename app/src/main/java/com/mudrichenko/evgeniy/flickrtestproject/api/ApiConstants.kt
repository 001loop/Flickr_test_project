package com.mudrichenko.evgeniy.flickrtestproject.api

import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.R

object ApiConstants {

    const val REST_URL = "https://api.flickr.com/services/rest"

    const val REQUEST_TOKEN_URL = "https://www.flickr.com/services/oauth/request_token"

    const val ACCESS_TOKEN_URL = "https://www.flickr.com/services/oauth/access_token"

    const val REST_CONSUMER_KEY = "d6ec61479a19d1d361f55d35a8d10242"

    const val REST_CONSUMER_SECRET = "8ff9851449f63828"

    var REST_CALLBACK_URL = String.format(App.appContext!!.resources.getString(R.string.app_rest_callback),
            App.appContext!!.resources.getString(R.string.app_rest_callback_scheme),
            App.appContext!!.resources.getString(R.string.app_rest_callback_host))

    const val RESPONSE_STAT_OK = "ok"
    const val RESPONSE_STAT_FAIL = "fail"

    const val RESPONSE_NO_ERROR_CODE = 0

    const val RESPONSE_ERROR_INVALID_SIGNATURE = "signature_invalid"

    const val RESPONSE_AUTH_TOKEN = "oauth_token"
    const val RESPONSE_AUTH_TOKEN_SECRET = "oauth_token_secret"
    const val RESPONSE_FULL_NAME = "fullname"
    const val RESPONSE_USER_NSID = "user_nsid"
    const val RESPONSE_USER_NAME = "username"

    const val PHOTO_SIZE_LABEL_SMALL = "Small 320"
    const val PHOTO_SIZE_LABEL_ORIGINAL_SIZE = "Original"


    const val GOOGLE_MAPS_LOCATION_NAME_UNNAMED = "Unnamed"

}