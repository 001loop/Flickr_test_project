package com.mudrichenko.evgeniy.flickrtestproject.utils

import android.content.Context
import android.net.ConnectivityManager

class NetworkUtils(val context: Context) {

    fun isInternetConnectionAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return false
        return cm.getActiveNetworkInfo() != null
    }

}