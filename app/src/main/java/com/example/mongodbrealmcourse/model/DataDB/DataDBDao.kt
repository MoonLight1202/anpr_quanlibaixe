package com.example.mongodbrealmcourse.model.DataDB

import androidx.room.*

@Dao
interface DataDBDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(item: DataDBItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateData(item: DataDBItem)

    @Query("DELETE FROM PlateNumberDB_ WHERE id = :id")
    fun deleteData(id: Int)

    @Query("SELECT * FROM PlateNumberDB_ WHERE id = :id")
    fun loadData(id: Int): DataDBItem?

    @Query("SELECT * FROM PlateNumberDB_")
    fun loadAll(): List<DataDBItem>
}