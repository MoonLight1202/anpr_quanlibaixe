package com.example.mongodbrealmcourse.viewmodel.utils

import android.content.Context

class PreferenceHelper(private val context: Context, name: String = PREFERENCES_NAME) {
    companion object {
        val globalHelper = GlobalHelper()
        val PREFERENCES_NAME = globalHelper.preferenceNameApp
    }

    private val sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    var current_account_email: String
        get() {
            return sharedPreferences.getString(
                globalHelper.currentAccount, "null"
            ) ?: "null"
        }
        set(value) {
            sharedPreferences.edit().putString(globalHelper.currentAccount, value).apply()
        }
    var current_account_password: String
        get() {
            return sharedPreferences.getString(
                globalHelper.currentPassword, "null"
            ) ?: "null"
        }
        set(value) {
            sharedPreferences.edit().putString(globalHelper.currentPassword, value).apply()
        }
}