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
    fun getEstate(estateKey: Long): LiveData<DetailedEstate> =
        estateRepository.getLiveEstate(estateKey)

    private val _allDetailedEstates: MutableLiveData<List<DetailedEstate>> = MutableLiveData()
    val allDetailedEstates: LiveData<List<DetailedEstate>> = _allDetailedEstates

    private var searchEstate: MutableLiveData<EstateSearch> = MutableLiveData()

    private fun updateSearchEstate(searchEstate: EstateSearch?) {
        this.searchEstate.value = searchEstate
    }

    fun filterEstates(searchEstate: EstateSearch?) {
        updateSearchEstate(searchEstate)
        viewModelScope.launch(Dispatchers.IO) {
            _allDetailedEstates.postValue(estateRepository.filterEstateList(searchEstate))
        }
    }

    fun getAllEstates() {
        viewModelScope.launch(Dispatchers.IO) {
            _allDetailedEstates.postValue(estateRepository.filterEstateList(null))
        }
    }

    fun initEstates(){
        if (searchEstate.value != null) filterEstates(searchEstate.value)
        else getAllEstates()
    }


    /**
     * Navigation for the EstateListDetail fragment.
     */
    private val _navigateToEstateDetail: MutableLiveData<Long> = MutableLiveData()
    val navigateToEstateDetail: LiveData<Long> = _navigateToEstateDetail

    private val _currentDetailEstate: MutableLiveData<DetailedEstate> = MutableLiveData()
    val currentDetailEstate: LiveData<DetailedEstate> = _currentDetailEstate

    fun onEstateClicked(estate: DetailedEstate) {
        viewModelScope.launch(Dispatchers.IO) {
            _navigateToEstateDetail.postValue(estate.estate!!.startTime)
            _currentDetailEstate.postValue(estateRepository.getEstate(estate.estate!!.startTime))
        }
    }

    fun stopEstateNavigation() {
            _navigateToEstateDetail.value = null
    }
}
