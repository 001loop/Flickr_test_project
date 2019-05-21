package com.mudrichenko.evgeniy.flickrtestproject.utils

import android.util.Base64
import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.api.ApiConstants
import java.net.URLEncoder
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class AuthUtils {

    @Inject
    lateinit var mPrefUtils: PrefUtils

    @Inject
    lateinit var mStringUtils: StringUtils

    init {
        App.appComponent!!.inject(this)
    }

    //          RequestTokenUrl
    fun getRequestTokenUrl(): String {
        val nonce = getNonce()
        val timestamp = getTimestamp()
        val encodedCallbackURL = URLEncoder.encode(ApiConstants.REST_CALLBACK_URL, "utf-8")
        val params = getRequestTokenParams(encodedCallbackURL, nonce, timestamp)
        val baseString = getBaseRequestTokenString(params)
        val secret64 = ApiConstants.REST_CONSUMER_SECRET + "&"
        val signature = getHMAC_SHA1(baseString, secret64)
        return getRequestTokenUrlStringWithSignature(params, signature)
    }

    private fun getRequestTokenParams(encodedCallbackURL: String, nonce: String, timestamp: String) =
            "oauth_callback=" + encodedCallbackURL +
                    "&oauth_consumer_key=" + ApiConstants.REST_CONSUMER_KEY +
                    "&oauth_nonce=" + nonce +
                    "&oauth_signature_method=HMAC-SHA1" +
                    "&oauth_timestamp=" + timestamp +
                    "&oauth_version=1.0"
    //      ================================================================================     //

    //          AccessTokenUrl
    fun getAccessTokenUrl(token: String, verifier: String): String {
        val nonce = getNonce()
        val timestamp = getTimestamp()
        val params = getAccessTokenParams(nonce, timestamp, token, verifier)
        val baseString = getBaseAccessTokenString(params)
        val secret64 = ApiConstants.REST_CONSUMER_SECRET + "&" + mPrefUtils.getAuthTokenSecret()
        val signature = getHMAC_SHA1(baseString, secret64)
        return getAccessTokenUrlStringWithSignature(params, signature)
    }

    private fun getAccessTokenParams(nonce: String, timestamp: String, token: String, verifier: String) =
            "oauth_consumer_key=" + ApiConstants.REST_CONSUMER_KEY +
                    "&oauth_nonce=" + nonce +
                    "&oauth_signature_method=HMAC-SHA1" +
                    "&oauth_timestamp=" + timestamp +
                    "&oauth_token=" + token +
                    "&oauth_verifier=" + verifier +
                    "&oauth_version=1.0"
    //      ================================================================================     //

    fun getUserAuthLink(authToken: String) =
            "https://www.flickr.com/services/oauth/authorize?oauth_token=$authToken"

    fun getAuthTokenFromResponse(response: String) =
            mStringUtils.getParameterFromResponseString(response, ApiConstants.RESPONSE_AUTH_TOKEN)

    fun getAuthTokenSecretFromResponse(response: String) =
            mStringUtils.getParameterFromResponseString(response, ApiConstants.RESPONSE_AUTH_TOKEN_SECRET)

    //          RecentPhotosUrl
    var METHOD_RECENT_PHOTOS = "flickr.interestingness.getList"

    fun getRecentPhotosUrl(currentPage: Int, numOfPhotosPerPage: Int): String {
        val token = mPrefUtils.getAuthToken() ?: return ""
        val nonce = getNonce()
        val timestamp = getTimestamp()
        val params = getRecentPhotosParams(nonce, timestamp, token, currentPage, numOfPhotosPerPage)
        val baseString = getBaseString(params)
        val secret64 = ApiConstants.REST_CONSUMER_SECRET + "&" + token
        val signature = getHMAC_SHA1(baseString, secret64)
        return getRestUrlStringWithSignature(params, signature)
    }

    private fun getRecentPhotosParams(nonce: String, timestamp: String, token: String, currentPage: Int, numOfPhotosPerPage: Int) =
            getBaseRestParamsString(nonce, timestamp, token, METHOD_RECENT_PHOTOS) +
                    "&page=" + currentPage +
                    "&per_page=" + numOfPhotosPerPage
    //      ================================================================================     //

    //          LocationPhotos
    var METHOD_LOCATION_PHOTOS = "flickr.photos.search"

    fun getLocationPhotosUrl(currentPage: Int, numOfPhotosPerPage: Int, lat: Double, lon: Double): String {
        val token = mPrefUtils.getAuthToken() ?: return ""
        val nonce = getNonce()
        val timestamp = getTimestamp()
        val params = getLocationPhotosParams(nonce, timestamp, token, currentPage, numOfPhotosPerPage, lat, lon)
        val baseString = getBaseString(params)
        val secret64 = ApiConstants.REST_CONSUMER_SECRET + "&" + token
        val signature = getHMAC_SHA1(baseString, secret64)
        return getRestUrlStringWithSignature(params, signature)
    }

    private fun getLocationPhotosParams(nonce: String, timestamp: String, token: String, currentPage: Int, numOfPhotosPerPage: Int, lat: Double, lon: Double) =
            getBaseRestParamsString(nonce, timestamp, token, METHOD_LOCATION_PHOTOS) +
                    "&page=" + currentPage +
                    "&per_page=" + numOfPhotosPerPage +
                    "&lat=" + lat +
                    "&lon=" + lon
    //      ================================================================================     //

    //          UserPhotos
    var METHOD_USER_PHOTOS = "flickr.people.getPhotos"

    fun getUserPhotosUrl(currentPage: Int, numOfPhotosPerPage: Int, userId: String?): String {
        val token = mPrefUtils.getAuthToken() ?: return ""
        val nonce = getNonce()
        val timestamp = getTimestamp()
        val params = getUserPhotosParams(nonce, timestamp, token, currentPage, numOfPhotosPerPage, userId)
        val baseString = getBaseString(params)
        val secret64 = ApiConstants.REST_CONSUMER_SECRET + "&" + token
        val signature = getHMAC_SHA1(baseString, secret64)
        return getRestUrlStringWithSignature(params, signature)
    }

    private fun getUserPhotosParams(nonce: String, timestamp: String, token: String,
                                    currentPage: Int, numOfPhotosPerPage: Int, userId: String?): String {
        val resultedId = userId ?: "me"
        return getBaseRestParamsString(nonce, timestamp, token, METHOD_USER_PHOTOS) +
                    "&page=" + currentPage +
                    "&per_page=" + numOfPhotosPerPage +
                    "&user_id=" + resultedId
    }
    //      ================================================================================     //

    //          PublicPhotos
    var METHOD_PUBLIC_PHOTOS = "flickr.photos.getContactsPublicPhotos"

    fun getPublicPhotosUrl(currentPage: Int, numOfPhotosPerPage: Int): String {
        val token = mPrefUtils.getAuthToken() ?: return ""
        val nonce = getNonce()
        val timestamp = getTimestamp()
        val params = getPublicPhotosParams(nonce, timestamp, token, currentPage, numOfPhotosPerPage)
        val baseString = getBaseString(params)
        val secret64 = ApiConstants.REST_CONSUMER_SECRET + "&" + token
        val signature = getHMAC_SHA1(baseString, secret64)
        return getRestUrlStringWithSignature(params, signature)
    }

    private fun getPublicPhotosParams(nonce: String, timestamp: String, token: String, currentPage: Int, numOfPhotosPerPage: Int) =
            getBaseRestParamsString(nonce, timestamp, token, METHOD_PUBLIC_PHOTOS) +
                    "&page=" + currentPage +
                    "&per_page=" + numOfPhotosPerPage +
                    "&user_id=" + mPrefUtils.getUserId()
    //      ================================================================================     //

    //          AlbumPhotos
    var METHOD_ALBUM_PHOTOS = "flickr.photosets.getPhotos"

    fun getAlbumPhotosUrl(currentPage: Int, numOfPhotosPerPage: Int, userId: String, photosetId: Long): String {
        val token = mPrefUtils.getAuthToken() ?: return ""
        val nonce = getNonce()
        val timestamp = getTimestamp()
        val params = getAlbumPhotosParams(nonce, timestamp, token, currentPage, numOfPhotosPerPage, userId, photosetId)
        val baseString = getBaseString(params)
        val secret64 = ApiConstants.REST_CONSUMER_SECRET + "&" + token
        val signature = getHMAC_SHA1(baseString, secret64)
        return getRestUrlStringWithSignature(params, signature)
    }

    private fun getAlbumPhotosParams(nonce: String, timestamp: String, token: String, currentPage: Int, numOfPhotosPerPage: Int, userId: String, photosetId: Long) =
            getBaseRestParamsString(nonce, timestamp, token, METHOD_ALBUM_PHOTOS) +
                    "&page=" + currentPage +
                    "&per_page=" + numOfPhotosPerPage +
                    "&photoset_id=" + photosetId +
                    "&user_id=" + userId
    //      ================================================================================     //

    //          PhotoInfo
    var METHOD_PHOTO_INFO = "flickr.photos.getInfo"

    fun getPhotoInfoUrl(photoId: Long): String {
        val token = mPrefUtils.getAuthToken() ?: return ""
        val nonce = getNonce()
        val timestamp = getTimestamp()
        val params = getPhotoInfoParams(nonce, timestamp, token, photoId)
        val baseString = getBaseString(params)
        val secret64 = ApiConstants.REST_CONSUMER_SECRET + "&" + token
        val signature = getHMAC_SHA1(baseString, secret64)
        return getRestUrlStringWithSignature(params, signature)
    }

    private fun getPhotoInfoParams(nonce: String, timestamp: String, token: String, photoId: Long) =
            getBaseRestParamsString(nonce, timestamp, token, METHOD_PHOTO_INFO) +
                    "&photo_id=" + photoId
    //      ================================================================================     //

    //          Photosets
    var METHOD_PHOTOSETS = "flickr.photosets.getList"

    fun getPhotosetsUrl(userId: String): String {
        val token = mPrefUtils.getAuthToken() ?: return ""
        val nonce = getNonce()
        val timestamp = getTimestamp()
        val params = getPhotosetsParams(nonce, timestamp, token, userId)
        val baseString = getBaseString(params)
        val secret64 = ApiConstants.REST_CONSUMER_SECRET + "&" + token
        val signature = getHMAC_SHA1(baseString, secret64)
        return getRestUrlStringWithSignature(params, signature)
    }

    private fun getPhotosetsParams(nonce: String, timestamp: String, token: String, userId: String) =
            getBaseRestParamsString(nonce, timestamp, token, METHOD_PHOTOSETS) +
                    "&user_id=" + userId
    //      ================================================================================     //

    //          Contact List
    var METHOD_CONTACT_LIST = "flickr.contacts.getList"

    fun getContactListUrl(): String {
        val token = mPrefUtils.getAuthToken() ?: return ""
        val nonce = getNonce()
        val timestamp = getTimestamp()
        val params = getContactListParams(nonce, timestamp, token)
        val baseString = getBaseString(params)
        val secret64 = ApiConstants.REST_CONSUMER_SECRET + "&" + mPrefUtils.getAuthTokenSecret()
        val signature = getHMAC_SHA1(baseString, secret64)
        return getRestUrlStringWithSignature(params, signature)
    }

    private fun getContactListParams(nonce: String, timestamp: String, token: String) =
            getBaseRestParamsString(nonce, timestamp, token, METHOD_CONTACT_LIST)
    //      ================================================================================     //

    //          Delete photo
    var METHOD_DELETE_PHOTO = "flickr.photos.delete"

    fun getDeletePhotoUrl(photoId: Long): String {
        val token = mPrefUtils.getAuthToken() ?: return ""
        val nonce = getNonce()
        val timestamp = getTimestamp()
        val params = getDeletePhotoParams(nonce, timestamp, token, photoId)
        val baseString = getBaseString(params)
        val secret64 = ApiConstants.REST_CONSUMER_SECRET + "&" + mPrefUtils.getAuthTokenSecret()
        val signature = getHMAC_SHA1(baseString, secret64)
        return getRestUrlStringWithSignature(params, signature)
    }

    private fun getDeletePhotoParams(nonce: String, timestamp: String, token: String, photoId: Long) =
            getBaseRestParamsString(nonce, timestamp, token, METHOD_DELETE_PHOTO) +
                    "&photo_id=" + photoId
    //      ================================================================================     //

    //          photoSizes
    var METHOD_PHOTO_SIZES = "flickr.photos.getSizes"

    fun getPhotoSizesUrl(photoId: Long): String {
        val token = mPrefUtils.getAuthToken() ?: return ""
        val nonce = getNonce()
        val timestamp = getTimestamp()
        val params = getPhotoSizesParams(nonce, timestamp, token, photoId)
        val baseString = getBaseString(params)
        val secret64 = ApiConstants.REST_CONSUMER_SECRET + "&" + token
        val signature = getHMAC_SHA1(baseString, secret64)
        return getRestUrlStringWithSignature(params, signature)
    }

    private fun getPhotoSizesParams(nonce: String, timestamp: String, token: String,
                                    photoId: Long): String {
        return getBaseRestParamsString(nonce, timestamp, token, METHOD_PHOTO_SIZES) +
                "&photo_id=" + photoId
    }
    //      ================================================================================     //

    private fun getBaseString(params: String) =
            "GET&" + URLEncoder.encode(ApiConstants.REST_URL, "utf-8") + "&" + URLEncoder.encode(params, "utf-8")

    private fun getBaseAccessTokenString(params: String) =
            "GET&" + URLEncoder.encode(ApiConstants.ACCESS_TOKEN_URL, "utf-8") + "&" + URLEncoder.encode(params, "utf-8")

    private fun getBaseRequestTokenString(params: String) =
            "GET&" + URLEncoder.encode(ApiConstants.REQUEST_TOKEN_URL, "utf-8") + "&" + URLEncoder.encode(params, "utf-8")

    private fun getBaseRestParamsString(nonce: String, timestamp: String, token: String, method: String) =
            "format=json" +
                    "&method=" + method +
                    "&nojsoncallback=1" +
                    "&oauth_consumer_key=" + ApiConstants.REST_CONSUMER_KEY +
                    "&oauth_nonce=" + nonce +
                    "&oauth_signature_method=HMAC-SHA1" +
                    "&oauth_timestamp=" + timestamp +
                    "&oauth_token=" + token +
                    "&oauth_version=1.0"

    private fun getRequestTokenUrlStringWithSignature(params: String, signature: String) =
            ApiConstants.REQUEST_TOKEN_URL + "?$params&oauth_signature=$signature"

    private fun getAccessTokenUrlStringWithSignature(params: String, signature: String) =
            ApiConstants.ACCESS_TOKEN_URL + "?$params&oauth_signature=$signature"

    private fun getRestUrlStringWithSignature(params: String, signature: String) =
            ApiConstants.REST_URL + "?$params&oauth_signature=$signature"

    private fun getTimestamp(): String = (System.currentTimeMillis() / 1000).toString()

    private fun getNonce(): String = System.nanoTime().toString()

    private fun getHMAC_SHA1(data: String, secret64: String): String {
        val key = SecretKeySpec(secret64.toByteArray(charset("UTF-8")), "HmacSHA1")
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(key)
        return Base64.encodeToString(mac.doFinal(data.toByteArray(charset("UTF-8"))), 0)
    }
}