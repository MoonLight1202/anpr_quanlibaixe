package com.example.mongodbrealmcourse.view.fragment

import androidx.fragment.app.Fragment
import com.example.mongodbrealmcourse.viewmodel.listener.HomeListener
import com.example.mongodbrealmcourse.viewmodel.utils.PreferenceHelper
import io.realm.mongodb.App
import io.realm.mongodb.mongo.MongoClient
import io.realm.mongodb.mongo.MongoDatabase

abstract class BaseFragment: Fragment() {
    protected val preferenceHelper: PreferenceHelper by lazy { PreferenceHelper(requireContext()) }
    protected var homeListener: HomeListener? = null
    protected lateinit var mongoDatabase: MongoDatabase
    protected lateinit var mongoClient: MongoClient
    protected val Appid = "number-plates-ayumd"
    protected lateinit var app: App

}