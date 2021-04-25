package com.openclassrooms.realestatemanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.database.model.Estate
import com.openclassrooms.realestatemanager.database.model.Picture
import com.openclassrooms.realestatemanager.repository.EstateRepository
import com.openclassrooms.realestatemanager.repository.PictureRepository


class EstateListViewModel(application: Application) : AndroidViewModel(application) {

    private val estateDao = EstateDatabase.getDatabase(application, viewModelScope).estateDao()
    private val estateRepository: EstateRepository = EstateRepository(estateDao)

    private val pictureDao = EstateDatabase.getDatabase(application, viewModelScope).pictureDao()
    private val pictureRepository: PictureRepository = PictureRepository(pictureDao)

    val allEstates: LiveData<List<Estate>> = estateRepository.allEstates
    val allDetailedEstates: LiveData<List<DetailedEstate>> = estateRepository.allDetailedEstates
    val allPictures: LiveData<List<Picture>> = pictureRepository.allPictures

    fun getEstateWithId(estateKey: Long): LiveData<DetailedEstate> {
        return estateRepository.getEstate(estateKey)
    }

    fun getEstatePictures(estateKey: Long): LiveData<List<Picture>> {
        return pictureRepository.getEstatePictures(estateKey)
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