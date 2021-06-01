package com.openclassrooms.realestatemanager.database.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.database.model.Estate
import com.openclassrooms.realestatemanager.database.model.EstateWithPoi

@Dao
interface EstateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estate: Estate): Long

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
    @Transaction
    @Query("SELECT * FROM estate_table WHERE start_time_milli = :start_time_milli")
    suspend fun getEstate(start_time_milli: Long): DetailedEstate

    @Transaction
    @Query("SELECT * FROM estate_table WHERE start_time_milli = :start_time_milli")
    fun getLiveEstate(start_time_milli: Long): LiveData<DetailedEstate>

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM estate_table ORDER BY start_time_milli DESC")
    fun getAllEstates(): LiveData<List<Estate>>

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM estate_table ORDER BY start_time_milli DESC")
    fun getAllCursorEstates(): Cursor

    @Transaction
    @Query("SELECT * from estate_table")
    suspend fun getDetailedEstates(): List<DetailedEstate>


    @Query("DELETE FROM estate_table")
    fun deleteAll()

    @Query("SELECT * FROM estate_table")
    fun getEstateList(): Cursor

    @Transaction
    @RawQuery
    suspend fun filterEstateList(searchEstate: SimpleSQLiteQuery): List<DetailedEstate>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(join: EstateWithPoi)

    @Query("DELETE FROM estate_with_poi_table WHERE estate_id = :estateId AND poi_id IN(:poiIds)")
    suspend fun deleteEstatePois(estateId: Long, poiIds: List<Int>)

    @Query("DELETE FROM estate_with_poi_table WHERE estate_id = :estateId")
    suspend fun deleteEstatePois(estateId: Long)
}
