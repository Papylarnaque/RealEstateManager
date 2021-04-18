package com.openclassrooms.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.Estate
import com.openclassrooms.realestatemanager.database.EstateDatabaseDao

class EstateRepository (private val estateDao: EstateDatabaseDao) {

    val allEstates: LiveData<List<Estate>> = estateDao.getAllEstates()

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


}