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


class ListDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val estateDao = EstateDatabase.getDatabase(application, viewModelScope).estateDao()
    private val estateRepository: EstateRepository = EstateRepository(estateDao)
    private val poiDao = EstateDatabase.getDatabase(application, viewModelScope).poiDao()
    private val poiRepository: PoiRepository = PoiRepository(poiDao)
    private val typeDao = EstateDatabase.getDatabase(application, viewModelScope).typeDao()
    private val typeRepository: TypeRepository = TypeRepository(typeDao)

    val allDetailedEstates: LiveData<List<DetailedEstate>> = estateRepository.allDetailedEstates
    fun allPois(): LiveData<List<Poi>> = poiRepository.allPois
    fun allTypes(): LiveData<List<Type>> = typeRepository.allTypes
    fun getEstate(id: Long): LiveData<DetailedEstate> = estateRepository.getEstate(id)

    /**
     * Navigation for the EstateListDetail fragment.
     */
    private val _navigateToEstateDetail = MutableLiveData<DetailedEstate>()
    val navigateToEstateDetail
        get() = _navigateToEstateDetail

    fun onEstateClicked(estate: DetailedEstate) {
        _navigateToEstateDetail.value = estate
    }

    fun filterEstateList(searchEstate: EstateSearch?): LiveData<List<DetailedEstate>> {
        return estateRepository.filterEstateList(searchEstate)
    }

}
