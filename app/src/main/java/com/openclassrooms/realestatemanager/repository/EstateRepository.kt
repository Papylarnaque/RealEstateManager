package com.openclassrooms.realestatemanager.repository

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.EstateDao
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.database.model.Estate

class EstateRepository(private val estateDao: EstateDao) {

    val allDetailedEstates: LiveData<List<DetailedEstate>> = estateDao.getDetailedEstates()

    suspend fun insert(estate: Estate) = estateDao.insert(estate)

    suspend fun update(estate: Estate) = estateDao.updateEstate(estate)

    fun getEstate(estateKey: Long): LiveData<DetailedEstate> = estateDao.getEstate(estateKey)

}


