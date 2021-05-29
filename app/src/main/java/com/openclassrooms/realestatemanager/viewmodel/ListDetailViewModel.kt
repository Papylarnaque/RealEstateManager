package com.openclassrooms.realestatemanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.database.model.EstateSearch
import com.openclassrooms.realestatemanager.database.model.Poi
import com.openclassrooms.realestatemanager.database.model.Type
import com.openclassrooms.realestatemanager.repository.EstateRepository
import com.openclassrooms.realestatemanager.repository.PoiRepository
import com.openclassrooms.realestatemanager.repository.TypeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ListDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val estateDao = EstateDatabase.getDatabase(application, viewModelScope).estateDao()
    private val estateRepository: EstateRepository = EstateRepository(estateDao)
    private val poiDao = EstateDatabase.getDatabase(application, viewModelScope).poiDao()
    private val poiRepository: PoiRepository = PoiRepository(poiDao)
    private val typeDao = EstateDatabase.getDatabase(application, viewModelScope).typeDao()
    private val typeRepository: TypeRepository = TypeRepository(typeDao)

    fun allPois(): LiveData<List<Poi>> = poiRepository.allPois
    fun allTypes(): LiveData<List<Type>> = typeRepository.allTypes

    private val _allDetailedEstates: MutableLiveData<List<DetailedEstate>> = MutableLiveData()
    val allDetailedEstates: LiveData<List<DetailedEstate>> = _allDetailedEstates

    private var filterStatus = false


    init {
        if (!filterStatus) getAllEstates()
    }

    private fun updateFilterStatus(boolean: Boolean) {
        filterStatus = boolean
    }

    fun filterEstates(searchEstate: EstateSearch?) {
        viewModelScope.launch(Dispatchers.IO) {
            updateFilterStatus(true)
            _allDetailedEstates.postValue(estateRepository.filterEstateList(searchEstate))
        }
    }

    fun getAllEstates() {
        viewModelScope.launch(Dispatchers.IO) {
            updateFilterStatus(false)
            _allDetailedEstates.postValue(estateRepository.filterEstateList(null))
        }
    }


    /**
     * Navigation for the EstateListDetail fragment.
     */
    private val _navigateToEstateDetail: MutableLiveData<DetailedEstate> = MutableLiveData()
    val navigateToEstateDetail: LiveData<DetailedEstate> = _navigateToEstateDetail

    fun onEstateClicked(estate: DetailedEstate) {
        viewModelScope.launch(Dispatchers.IO) {
            _navigateToEstateDetail.postValue(estateRepository.getEstate(estate.estate!!.startTime))
        }
    }
}
