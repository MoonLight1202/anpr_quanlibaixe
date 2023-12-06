package com.example.mongodbrealmcourse.model.`object`

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class PlateNumberObject(
    @ColumnInfo (name = "id_user") val id_user: String,
    @ColumnInfo (name = "info_plate") val infoPlate: String,
    @ColumnInfo (name = "region") val region: String,
    @ColumnInfo (name = "type_car") val typeCar: String,
    @ColumnInfo (name = "pay") val pay: String,
    @ColumnInfo (name = "accuracy") val accuracy: Float,
    @ColumnInfo (name = "img_plate") val imgPlate: String,
    @ColumnInfo (name = "img_plate_cut") val imgPlateCut: String,
    @ColumnInfo (name = "date_create") val dateCreate: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readFloat(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        Log.d("TAG_AA", "write")
    }

    companion object CREATOR : Parcelable.Creator<PlateNumberObject> {
        override fun createFromParcel(parcel: Parcel): PlateNumberObject {
            return PlateNumberObject(parcel)
        }

        override fun newArray(size: Int): Array<PlateNumberObject?> {
            return arrayOfNulls(size)
        }
    }
}