package com.openclassrooms.realestatemanager.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EstateDatabaseDao {

    @Insert
    suspend fun insert(estate: Estate)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param estate new value to write
     */
    @Update
    suspend  fun update(estate: Estate)

    /**
     * Selects and returns the row that matches the supplied start time, which is our key.
     *
     * @param key startTimeMilli to match
     */
    @Query("SELECT * FROM estate_table WHERE estateId = :key")
    suspend fun get(key: Long): Estate?

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM estate_table ORDER BY estateId DESC")
    fun getAllEstates(): LiveData<List<Estate>>

    /**
     * Deletes the row that matches the supplied start time, which is our key.
     *
     * @param key startTimeMilli to match
     */
    @Query("DELETE FROM estate_table WHERE estateId = :key")
    suspend fun delete(key: Long)
}
