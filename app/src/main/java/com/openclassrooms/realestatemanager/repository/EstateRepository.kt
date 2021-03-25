package com.openclassrooms.realestatemanager.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.Estate
import com.openclassrooms.realestatemanager.database.EstateDatabaseDao

class EstateRepository (private val estateDao: EstateDatabaseDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allEstates: LiveData<List<Estate>> = estateDao.getAllEstates()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(estate: Estate) {
        estateDao.insert(estate)
    }
}