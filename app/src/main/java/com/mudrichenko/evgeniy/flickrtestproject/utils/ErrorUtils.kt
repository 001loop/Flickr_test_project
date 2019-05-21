package com.mudrichenko.evgeniy.flickrtestproject.utils

import com.mudrichenko.evgeniy.flickrtestproject.App
import com.mudrichenko.evgeniy.flickrtestproject.R
class ErrorUtils {

    companion object {
        const val ERROR_CODE_UNKNOWN = -1
        const val ERROR_CODE_INTERNET = -2
        const val ERROR_CODE_SERVER = -3
        const val ERROR_CODE_INCORRECT_DATA_FROM_SERVER = -4
        const val ERROR_CODE_DATABASE = -5
    }

    /***
     *  in case with unknown error you don`t need to send error message string,
     *  just send null message string
     */

    fun getErrorMessage(code: Int, message: String?): String {
        if (message == null) {
            return App.appContext!!.resources.getString(R.string.error_unknown)
        }
        when(code) {
            ERROR_CODE_UNKNOWN -> return App.appContext!!.resources.getString(R.string.error_unknown)
            ERROR_CODE_INTERNET -> return App.appContext!!.resources.getString(R.string.error_unknown_internet)
            ERROR_CODE_SERVER -> return App.appContext!!.resources.getString(R.string.error_unknown_server)
            ERROR_CODE_INCORRECT_DATA_FROM_SERVER -> return App.appContext!!.resources.getString(R.string.error_incorrect_server_data)
            ERROR_CODE_DATABASE -> return App.appContext!!.resources.getString(R.string.error_database)
        }
        return String.format(App.appContext!!.resources.getString(R.string.error_description), code, message)
    }

}