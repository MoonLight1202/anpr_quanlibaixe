package com.example.mongodbrealmcourse.model.DataDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DataDBItem::class], version = 1)
abstract class DataDB: RoomDatabase() {
    abstract fun dataDBDao(): DataDBDao

    companion object {
        const val KEY_DB_NAME = "PlateNumberDB_"

        @Volatile
        private var INSTANCE: DataDB? = null

        private fun getDatabase(context: Context): DataDB {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, DataDB::class.java, KEY_DB_NAME
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }

        fun insertData(context: Context, item: DataDBItem) {
            if (checkExist(context, item.id)) {
                updateData(context, item)
                return
            }
            val dataDBDao = getDatabase(context).dataDBDao()
            dataDBDao.insertData(item)
        }


        fun loadData(context: Context, id: Int): DataDBItem? {
            val dataDBDao = getDatabase(context).dataDBDao()
            return dataDBDao.loadData(id)
        }

        private fun updateData(context: Context, questionItem: DataDBItem) {
            val dataDBDao = getDatabase(context).dataDBDao()
            dataDBDao.updateData(questionItem)
        }

        fun checkExist(context: Context, id: Int): Boolean {
            loadData(context, id)?.let {
                return true
            }
            return false
        }

        fun loadAllData(context: Context): List<DataDBItem> {
            val dataDBDao = getDatabase(context).dataDBDao()
            return dataDBDao.loadAll()
        }
    }

}