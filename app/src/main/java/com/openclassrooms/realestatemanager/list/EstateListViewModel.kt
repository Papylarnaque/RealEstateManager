package com.openclassrooms.realestatemanager.list

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
    
    /**
     * Navigation for the EstateListDetail fragment.
     */
    private val _navigateToEstateDetail = MutableLiveData<Long>()
    val navigateToEstateDetail
        get() = _navigateToEstateDetail

    fun onEstateClicked(id: Long) {
        _navigateToEstateDetail.value = id
    }


}