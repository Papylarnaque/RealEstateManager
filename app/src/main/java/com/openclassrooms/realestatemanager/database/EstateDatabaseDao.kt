package com.openclassrooms.realestatemanager.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EstateDatabaseDao {

    @Insert
    suspend fun insertEstate(estate: Estate)


    @Insert
    suspend fun insertPicture(picture: Picture)


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
     * @param estateId startTimeMilli to match
     */
    @Query("SELECT * FROM estate_table WHERE start_time_milli = :start_time_milli")
    suspend fun getEstate(start_time_milli: Long): Estate?

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM estate_table ORDER BY start_time_milli DESC")
    fun getAllEstates(): LiveData<List<Estate>>


    /**
     * Selects and returns all pictures for an estate,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM picture_table WHERE start_time_milli = :start_time_milli ")
    fun getPictures(start_time_milli: Long): LiveData<List<Picture>>



    /**
     * Deletes the row that matches the supplied estateId.
     *
     * @param estateId
     */
    @Query("DELETE FROM estate_table WHERE start_time_milli = :start_time_milli")
    suspend fun deleteEstate(start_time_milli: Long)


    /**
     * Deletes the row that matches the supplied estateId.
     *
     * @param pictureId
     */
    @Query("DELETE FROM picture_table WHERE pictureId = :pictureId AND picture_url = :pictureUrl")
    suspend fun deletePicture(pictureId: Long, pictureUrl: String)
}
