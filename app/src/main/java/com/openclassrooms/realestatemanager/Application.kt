package com.openclassrooms.realestatemanager

import androidx.multidex.MultiDexApplication
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.repository.EstateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class Application : MultiDexApplication() {

    private val applicationScope = CoroutineScope(SupervisorJob())
    private val db by lazy { EstateDatabase.getDatabase(this, applicationScope) }
    val estateRepository by lazy {
        EstateRepository(
            db.estateDao()
        )
    }
}