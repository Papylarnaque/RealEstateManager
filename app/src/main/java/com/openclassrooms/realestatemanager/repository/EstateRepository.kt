package com.openclassrooms.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.EstateDao
import com.openclassrooms.realestatemanager.database.model.Estate
import com.openclassrooms.realestatemanager.database.model.EstateAllPictures

class EstateRepository (private val estateDao: EstateDao) {

    val allEstates: LiveData<List<Estate>> = estateDao.getAllEstates()
    val allEstatesWithPictures: LiveData<List<EstateAllPictures>> = estateDao.getAllEstateWithPictures()

    suspend fun insert(estate: Estate) {
        estateDao.insert(estate)
    }

    suspend fun update(estate: Estate) {
        estateDao.updateEstate(estate)
        Log.i("EstateUpdate", "${estate.estatePrice}")
    }

    fun getEstate(estateKey: Long): LiveData<Estate> {
        return estateDao.getEstate(estateKey)
    }

    fun getAllEstateWithPictures(): LiveData<List<EstateAllPictures>> {
        return estateDao.getAllEstateWithPictures()
    }

}