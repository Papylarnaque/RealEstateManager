package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.database.model.Poi

@Dao
interface PoiDao {

    @Insert
    suspend fun insert(poi: Poi)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param poi new value to write
     */
    @Update
    suspend fun updatePoi(poi: Poi)

    /**
     * Selects and returns the row that matches the supplied estateId.
     *
     * @param poiId to match
     */
    @Query("SELECT * FROM poi_table WHERE poi_id = :poiId")
    fun getPoi(poiId: Long): LiveData<Poi>

    /**
     * Selects and returns all rows in the table,
     */
    @Query("SELECT * FROM poi_table")
    fun getAllPois(): LiveData<List<Poi>>


    @Query("DELETE FROM poi_table")
    fun deleteAll()

}