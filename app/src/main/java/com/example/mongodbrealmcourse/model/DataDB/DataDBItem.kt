package com.example.mongodbrealmcourse.model.DataDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val KEY_DB_NAME = "PlateNumberDB_"
@Entity(tableName = KEY_DB_NAME)
class DataDBItem(
    @PrimaryKey
    val id: Int,
    @ColumnInfo (name = "info_plate") val infoPlate: String?,
    @ColumnInfo (name = "region") val region: String?,
    @ColumnInfo (name = "type_car") val typeCar: String?,
    @ColumnInfo (name = "accuracy") val accuracy: Float?,
    @ColumnInfo (name = "img_plate") val imgPlate: String?,
    @ColumnInfo (name = "date_create") val dateCreate: String?,
)