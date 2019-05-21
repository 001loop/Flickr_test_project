package com.mudrichenko.evgeniy.flickrtestproject.utils

import java.io.IOException
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.net.HttpURLConnection


class ImageUtils {

    fun getBitmapFromUrl(srcUrl: String): Bitmap? {
        return try {
            val url = java.net.URL(srcUrl)
            val connection = url
                    .openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}