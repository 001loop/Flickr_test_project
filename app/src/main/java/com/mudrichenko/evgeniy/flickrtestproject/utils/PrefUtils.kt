package com.mudrichenko.evgeniy.flickrtestproject.utils

import android.content.Context
import android.content.SharedPreferences
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class PrefUtils(val context: Context) {

    @Inject
    lateinit var mStringUtils: StringUtils

    private val USER_PREFERENCES_NAME = "user_preferences"

    // auth
    private val AUTH_TOKEN = "auth_token"
    private val AUTH_TOKEN_SECRET = "auth_token_secret"
    //

    private val FLICKR_USER_ID = "user_id"
    private val FLICKR_USER_NAME = "user_name"
    private val FLICKR_USER_FULL_NAME = "user_full_name"

    private val LOCATION_NAME = "location_name"
    private val LOCATION_COORDINATES = "location_coordinates"

    private val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
        App.appComponent!!.inject(this)
    }


    private fun putPrefByKey(propertyName: String, value: String?) =
            preferences.edit().putString(propertyName, value).apply()

    private fun getPrefByKey(propertyName: String): String {
        return preferences.getString(propertyName, "")
    }

    fun putUserId(userId: String) = putPrefByKey(FLICKR_USER_ID, userId)

    fun getUserId(): String {
        return getPrefByKey(FLICKR_USER_ID)
    }

    fun putUserName(userName: String) = putPrefByKey(FLICKR_USER_NAME, userName)

    fun getUserName(): String {
        return getPrefByKey(FLICKR_USER_NAME)
    }

    fun putUserFullname(userName: String) = putPrefByKey(FLICKR_USER_FULL_NAME, userName)

    fun getUserFullname(): String {
        return getPrefByKey(FLICKR_USER_FULL_NAME)
    }

    fun putLocationName(locationName: String) = putPrefByKey(LOCATION_NAME, locationName)

    fun getLocationName(): String {
        return getPrefByKey(LOCATION_NAME)
    }

    fun putLocationLatLng(latLng: LatLng?) {
        var locationCoordinates: String? = null
        if (latLng != null) {
            locationCoordinates = latLng.toString()
        }
        putPrefByKey(LOCATION_COORDINATES, locationCoordinates)
    }

    fun getLocationLatLng(): LatLng? {
        return mStringUtils.getLatLngFromString(getPrefByKey(LOCATION_COORDINATES))
    }

    fun putAuthToken(authToken: String?) = putPrefByKey(AUTH_TOKEN, authToken)

    fun getAuthToken(): String? {
        val authToken = getPrefByKey(AUTH_TOKEN)
        if (authToken.isBlank()) {
            return null
        }
        return authToken
    }

    fun putAuthTokenSecret(authTokenSecret: String) = putPrefByKey(AUTH_TOKEN_SECRET, authTokenSecret)

    fun getAuthTokenSecret(): String {
        return getPrefByKey(AUTH_TOKEN_SECRET)
    }

}