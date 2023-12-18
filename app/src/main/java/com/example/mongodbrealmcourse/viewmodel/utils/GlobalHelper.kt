package com.example.mongodbrealmcourse.viewmodel.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import java.io.File
import java.io.FileWriter
import java.io.IOException

class GlobalHelper {
    val preferenceNameApp = "ANPR"
    val currentAccount = "current_account"
    val currentPassword = "current_password"
    val rememberAccount = "remember_account"
    val price_motorbike_hour = "price_motorbike_hour"
    val price_motorbike_day = "price_motorbike_day"
    val price_motorbike_month = "price_motorbike_month"
    val price_car_hour = "price_car_hour"
    val price_car_day = "price_car_day"
    val price_car_month = "price_car_month"
    val state_filter_lib = "state_filter_lib"


    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}