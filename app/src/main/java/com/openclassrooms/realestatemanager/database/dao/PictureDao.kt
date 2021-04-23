package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.database.model.Picture

@Dao
interface PictureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(picture: Picture)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param picture new value to write
     */
    @Update
    suspend fun updatePicture(picture: Picture)

    /**
     * Selects and returns the row that matches the supplied estateId.
     *
     * @param estateId to match
     */
    @Query("SELECT * FROM picture_table WHERE estate_id = :estateId")
    fun getEstatePictures(estateId: Long): LiveData<List<Picture>>

    /**
     * Selects and returns all rows in the table,
     */
    @Query("SELECT * FROM picture_table ORDER BY order_number")
    fun getAllPictures(): LiveData<List<Picture>>

    @Query("DELETE FROM poi_table")
    fun deleteAll()

}