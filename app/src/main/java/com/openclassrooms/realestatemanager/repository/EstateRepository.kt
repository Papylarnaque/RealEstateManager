package com.openclassrooms.realestatemanager.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.EstateDao
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.database.model.Estate

class EstateRepository (private val estateDao: EstateDao) {

    val allEstates: LiveData<List<Estate>> = estateDao.getAllEstates()
    val allDetailedEstates: LiveData<List<DetailedEstate>> = estateDao.getDetailedEstates()

    suspend fun insert(estate: Estate) {
        estateDao.insert(estate)
    }

    suspend fun update(estate: Estate) {
        estateDao.updateEstate(estate)
        Log.i("EstateUpdate", "${estate.estatePrice}")
    }

    fun getEstate(estateKey: Long): LiveData<DetailedEstate> {
        return estateDao.getEstate(estateKey)
    }
}