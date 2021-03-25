package com.openclassrooms.realestatemanager.creation

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.database.Estate
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.repository.EstateRepository


class CreationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EstateRepository
    private val allEstates: LiveData<List<Estate>>

    init {
        val estateDatabaseDao = EstateDatabase.getDatabase(application, viewModelScope).estateDatabaseDao()
        repository = EstateRepository(estateDatabaseDao)
        allEstates = repository.allEstates
    }


    suspend fun createNewEstate(documentUri: Uri?, checked1: Boolean, checked2: Boolean) {

        val estate = Estate(
                estateType = "Test",
                estateEmployee = "Etienne",
                pictureUrl = documentUri.toString())


        repository.insert(estate)
        Log.i("CreationViewModel","added new estate : ${estate.estateType}")
    }


}