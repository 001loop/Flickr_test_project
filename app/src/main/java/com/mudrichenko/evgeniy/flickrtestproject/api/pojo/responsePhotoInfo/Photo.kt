package com.mudrichenko.evgeniy.flickrtestproject.api.pojo.responsePhotoInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Photo {

    @SerializedName("id")
    @Expose
    var id: Long? = null
    @SerializedName("secret")
    @Expose
    var secret: String? = null
    @SerializedName("server")
    @Expose
    var server: String? = null
    @SerializedName("farm")
    @Expose
    var farm: Int? = null
    @SerializedName("dateuploaded")
    @Expose
    var dateuploaded: String? = null
    @SerializedName("isfavorite")
    @Expose
    var isfavorite: Int? = null
    @SerializedName("license")
    @Expose
    var license: String? = null
    @SerializedName("safety_level")
    @Expose
    var safetyLevel: String? = null
    @SerializedName("rotation")
    @Expose
    var rotation: Int? = null
    @SerializedName("owner")
    @Expose
    var owner: Owner? = null
    @SerializedName("title")
    @Expose
    var title: Title? = null
    @SerializedName("description")
    @Expose
    var description: Description? = null
    @SerializedName("visibility")
    @Expose
    var visibility: Visibility? = null
    @SerializedName("dates")
    @Expose
    var dates: Dates? = null
    @SerializedName("views")
    @Expose
    var views: Int? = null
    @SerializedName("editability")
    @Expose
    var editability: Editability? = null
    @SerializedName("publiceditability")
    @Expose
    var publiceditability: Publiceditability? = null
    @SerializedName("usage")
    @Expose
    var usage: Usage? = null
    @SerializedName("comments")
    @Expose
    var comments: Comments? = null
    @SerializedName("notes")
    @Expose
    var notes: Notes? = null
    @SerializedName("people")
    @Expose
    var people: People? = null
    @SerializedName("tags")
    @Expose
    var tags: Tags? = null
    @SerializedName("location")
    @Expose
    var location: Location? = null
    @SerializedName("geoperms")
    @Expose
    var geoperms: Geoperms? = null
    @SerializedName("urls")
    @Expose
    var urls: Urls? = null
    @SerializedName("media")
    @Expose
    var media: String? = null

}
