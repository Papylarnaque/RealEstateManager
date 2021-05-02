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
     * replaces the old url with the new one.
     *
     * @param oldUrl replaced by
     * @param pictureUrl new value to write
     */
    @Query("UPDATE picture_table SET  picture_url= :pictureUrl WHERE picture_url= :oldUrl")
    suspend fun replaceUrl(oldUrl: String, pictureUrl: String)


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

    @Query("DELETE FROM picture_table")
    fun deleteAll()

    @Query("DELETE FROM picture_table WHERE picture_url= :pictureUrl")
    fun deletePicture(pictureUrl: String)

}