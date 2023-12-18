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
    var state_filter_lib: String
        get() {
            return sharedPreferences.getString(
                globalHelper.state_filter_lib, "All"
            ) ?: "All"
        }
        set(value) {
            sharedPreferences.edit().putString(globalHelper.state_filter_lib, value).apply()
        }
    var remember_account: Boolean
        get() {
            return sharedPreferences.getBoolean(globalHelper.rememberAccount, false)
        }
        set(value) {
            sharedPreferences.edit().putBoolean(globalHelper.rememberAccount, value).apply()
        }
    var price_motorbike_hour: Int
        get() {
            return sharedPreferences.getInt(globalHelper.price_motorbike_hour, 0)
        }
        set(value) {
            sharedPreferences.edit().putInt(globalHelper.price_motorbike_hour, value).apply()
        }
    var price_motorbike_day: Int
        get() {
            return sharedPreferences.getInt(globalHelper.price_motorbike_day, 0)
        }
        set(value) {
            sharedPreferences.edit().putInt(globalHelper.price_motorbike_day, value).apply()
        }
    var price_motorbike_month: Int
        get() {
            return sharedPreferences.getInt(globalHelper.price_motorbike_month, 0)
        }
        set(value) {
            sharedPreferences.edit().putInt(globalHelper.price_motorbike_month, value).apply()
        }
    var price_car_hour: Int
        get() {
            return sharedPreferences.getInt(globalHelper.price_car_hour, 0)
        }
        set(value) {
            sharedPreferences.edit().putInt(globalHelper.price_car_hour, value).apply()
        }
    var price_car_day: Int
        get() {
            return sharedPreferences.getInt(globalHelper.price_car_day, 0)
        }
        set(value) {
            sharedPreferences.edit().putInt(globalHelper.price_car_day, value).apply()
        }
    var price_car_month: Int
        get() {
            return sharedPreferences.getInt(globalHelper.price_car_month, 0)
        }
        set(value) {
            sharedPreferences.edit().putInt(globalHelper.price_car_month, value).apply()
        }

}