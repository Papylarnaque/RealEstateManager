package com.openclassrooms.realestatemanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.database.Estate
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.repository.EstateRepository

class EstateListViewModel(application: Application) : AndroidViewModel(application) {

    private val estateDatabaseDao = EstateDatabase.getDatabase(application, viewModelScope).estateDatabaseDao()
    private val repository: EstateRepository = EstateRepository(estateDatabaseDao)
    val allEstates: LiveData<List<Estate>> = repository.allEstates



    fun getEstateWithId(estateKey: Long): LiveData<Estate> {
        return repository.getEstate(estateKey)
    }


    /**
     * Navigation for the EstateListDetail fragment.
     */
    private val _navigateToEstateDetail = MutableLiveData<Estate>()
    val navigateToEstateDetail
        get() = _navigateToEstateDetail

    fun onEstateClicked(estate: Estate) {
        _navigateToEstateDetail.value = estate
    }


}