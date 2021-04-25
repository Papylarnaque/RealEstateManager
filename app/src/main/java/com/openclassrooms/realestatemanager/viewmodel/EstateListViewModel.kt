package com.openclassrooms.realestatemanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.repository.EstateRepository


class EstateListViewModel(application: Application) : AndroidViewModel(application) {

    private val estateDao = EstateDatabase.getDatabase(application, viewModelScope).estateDao()
    private val estateRepository: EstateRepository = EstateRepository(estateDao)

    val allDetailedEstates: LiveData<List<DetailedEstate>> = estateRepository.allDetailedEstates

    fun getEstateWithId(estateKey: Long): LiveData<DetailedEstate> {
        return estateRepository.getEstate(estateKey)
    }

    /**
     * Navigation for the EstateListDetail fragment.
     */
    private val _navigateToEstateDetail = MutableLiveData<DetailedEstate>()
    val navigateToEstateDetail
        get() = _navigateToEstateDetail

    fun onEstateClicked(estate: DetailedEstate) {
        _navigateToEstateDetail.value = estate
    }


}