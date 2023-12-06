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
    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}