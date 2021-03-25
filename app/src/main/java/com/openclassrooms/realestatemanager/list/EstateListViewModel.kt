package com.openclassrooms.realestatemanager.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.database.Estate
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.repository.EstateRepository

class EstateListViewModel(application: Application) : AndroidViewModel(application) {

    private val estateDatabaseDao = EstateDatabase.getDatabase(application, viewModelScope).estateDatabaseDao()
    private val repository: EstateRepository = EstateRepository(estateDatabaseDao)
    val allEstates: LiveData<List<Estate>> = repository.allEstates

    var estate: Estate? = allEstates.value?.get(0)


}