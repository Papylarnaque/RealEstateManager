package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.database.model.Estate
import com.openclassrooms.realestatemanager.database.model.EstateAllPictures

@Dao
interface EstateDao {

    @Insert
    suspend fun insert(estate: Estate)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param estate new value to write
     */
    @Update
    suspend fun updateEstate(estate: Estate)

    /**
     * Selects and returns the row that matches the supplied estateId.
     *
     * @param start_time_milli to match
     */
    @Query("SELECT * FROM estate_table WHERE start_time_milli = :start_time_milli")
    fun getEstate(start_time_milli: Long): LiveData<Estate>

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM estate_table ORDER BY start_time_milli DESC")
    fun getAllEstates(): LiveData<List<Estate>>


    @Query("SELECT * from estate_table")
    fun getAllEstateWithPictures(): LiveData<List<EstateAllPictures>>


    @Query("DELETE FROM estate_table")
    fun deleteAll()



}
