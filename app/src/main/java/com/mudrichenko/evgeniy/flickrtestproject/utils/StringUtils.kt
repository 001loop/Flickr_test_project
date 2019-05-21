package com.mudrichenko.evgeniy.flickrtestproject.utils

import android.content.Context
import com.mudrichenko.evgeniy.flickrtestproject.FlickrPrivacy
import com.mudrichenko.evgeniy.flickrtestproject.R
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo.Location
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo.Owner
import com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo.Tag
import com.google.android.gms.maps.model.LatLng
import com.orhanobut.logger.Logger
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class StringUtils(val context: Context) {

    fun buildImageUrl(id: String, secret: String, server: Int, farm: Int) =
            "http://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + ".jpg"

    fun getLatLngFromString(latLng: String?): LatLng? {
        if (latLng == null) {
            return null
        }
        if (!latLng.contains(",")) {
            return null
        }
        val latLngString = latLng.replace("lat/lng: (", "").replace(")", "")
        val latLngArray = latLngString.split(",")
        var latitude = 0.0
        var longitude = 0.0
        try {
            latitude = java.lang.Double.parseDouble(latLngArray[0])
            longitude = java.lang.Double.parseDouble(latLngArray[1])
        } catch (e: NumberFormatException) {
            Logger.e("NumberFormatException")
        }
        return LatLng(latitude, longitude)
    }

    fun getParameterFromResponseString(source: String?, parameter: String?): String {
        val DELIMITER = "&"
        val START_SYMBOL = "="
        val DEFAULT_STRING = ""
        if (source == null || parameter == null) {
            return DEFAULT_STRING
        }
        if (source.isEmpty() || parameter.isEmpty()) {
            return DEFAULT_STRING
        }
        val formattedParameter = parameter + START_SYMBOL
        if (!source.contains(formattedParameter)) {
            return DEFAULT_STRING
        }
        val startIndex = source.indexOf(formattedParameter) + formattedParameter.length
        var endIndex = -1
        val stringFromStart = source.substring(startIndex)
        if (stringFromStart.contains(DELIMITER)) {
            endIndex = stringFromStart.indexOf(DELIMITER)
        }
        val encodedString = if (endIndex != -1) {
            stringFromStart.substring(0, endIndex)
        } else {
            stringFromStart
        }
        try {
            return URLDecoder.decode(encodedString,"utf-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return DEFAULT_STRING
    }

    fun getOwnerName(owner: Owner?): String {
        var ownerName = ""
        if (owner == null) {
            return ownerName
        }
        if (owner.userName != "") {
            ownerName += owner.userName
        }
        if (owner.realName != "") {
            ownerName += " (" + owner.realName + ")"
        }
        return ownerName
    }

    fun getOwnerName(nickName: String?, realName: String?): String {
        var ownerName = ""
        if (nickName == null) {
            return ownerName
        }
        ownerName += nickName
        if (realName != null && realName != "") {
            ownerName += " ($realName)"
        }
        return ownerName
    }

    fun getLocationName(location: Location?): String {
        var locationName = ""
        if (location == null) {
            return locationName
        }
        if (location.country != null) {
            locationName += location.country!!.content
        }
        if (location.region != null) {
            locationName += ", " + location.region!!.content
        }
        return locationName
    }

    fun getTagsString(tagList: List<Tag>?): String {
        var tagsString = ""
        if (tagList == null) {
            return tagsString
        }
        if (tagList.isEmpty()) {
            return tagsString
        }
        for (x in tagList.indices) {
            tagsString += (tagList[x].raw)
            if (x + 1 < tagList.size) {
                tagsString += ", "
            }
        }
        return tagsString
    }

    fun getLicenseStringById(licenseId: String): String {
        val resourceString = "photo_license_$licenseId"
        val resourceId = context.resources.getIdentifier(resourceString, "string", context.packageCodePath)
        return if (resourceId > 0) {
            context.getString(resourceId)
        } else {
            context.getString(R.string.photo_license_0)
        }
    }

    fun getPrivacyStringById(privacyId: Int): String {
        return when (privacyId) {
            FlickrPrivacy.PRIVACY_PUBLIC -> context.getString(R.string.photo_privacy_public)
            FlickrPrivacy.PRIVACY_FRIENDS_ONLY -> context.getString(R.string.photo_privacy_friends)
            FlickrPrivacy.PRIVACY_FAMILY_ONLY -> context.getString(R.string.photo_privacy_family)
            FlickrPrivacy.PRIVACY_FRIENDS_AND_FAMILY -> context.getString(R.string.photo_privacy_friends_and_family)
            FlickrPrivacy.PRIVACY_PRIVATE -> context.getString(R.string.photo_privacy_private)
            else -> context.getString(R.string.photo_privacy_public)
        }
    }

}