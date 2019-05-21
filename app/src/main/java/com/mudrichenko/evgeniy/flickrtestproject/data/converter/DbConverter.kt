package com.mudrichenko.evgeniy.flickrtestproject.data.converter

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DbConverter {
    @TypeConverter
    fun fromString(value: String?): ArrayList<String>? {
        if (value == null) {
            return null
        }
        val listType = object : TypeToken<ArrayList<String>>() {

        }.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<String>?): String? {
        if (list == null) {
            return null
        }
        val gson = Gson()
        return gson.toJson(list)
    }
}