package com.openclassrooms.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.PictureDao
import com.openclassrooms.realestatemanager.database.model.Picture

class PictureRepository(private val pictureDao: PictureDao) {

    val allPictures: LiveData<List<Picture>> = pictureDao.getAllPictures()

    suspend fun insert(picture: Picture) {
        pictureDao.insert(picture)
    }

    suspend fun update(picture: Picture) {
        pictureDao.updatePicture(picture)
        Log.i("Picture update", picture.url)
    }

    fun getEstatePictures(estateKey: Long): LiveData<List<Picture>> {
        return pictureDao.getEstatePictures(estateKey)
    }

}