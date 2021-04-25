package com.openclassrooms.realestatemanager.repository

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.PoiDao
import com.openclassrooms.realestatemanager.database.model.Poi

class PoiRepository(private val poiDao: PoiDao) {

    val allPois: LiveData<List<Poi>> = poiDao.getAllPois()

    suspend fun insert(poi: Poi) {
        poiDao.insert(poi)
    }

    suspend fun update(poi: Poi) {
        poiDao.updatePoi(poi)
    }

}