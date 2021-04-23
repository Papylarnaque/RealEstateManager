package com.openclassrooms.realestatemanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.model.Estate
import com.openclassrooms.realestatemanager.database.model.EstateAllPictures
import com.openclassrooms.realestatemanager.database.model.Picture
import com.openclassrooms.realestatemanager.repository.EstateRepository
import com.openclassrooms.realestatemanager.repository.PictureRepository


class EstateListViewModel(application: Application) : AndroidViewModel(application) {

    private val estateDao = EstateDatabase.getDatabase(application, viewModelScope).estateDao()
    private val estateRepository: EstateRepository = EstateRepository(estateDao)

    private val pictureDao = EstateDatabase.getDatabase(application, viewModelScope).pictureDao()
    private val pictureRepository: PictureRepository = PictureRepository(pictureDao)

    val allEstates: LiveData<List<Estate>> = estateRepository.allEstates
    val allEstatesWithPictures: LiveData<List<EstateAllPictures>> = estateRepository.allEstatesWithPictures
    val allPictures: LiveData<List<Picture>> = pictureRepository.allPictures

    fun getEstateWithId(estateKey: Long): LiveData<Estate> {
        return estateRepository.getEstate(estateKey)
    }

    fun getEstatePictures(estateKey: Long): LiveData<List<Picture>> {
        return pictureRepository.getEstatePictures(estateKey)
    }

    fun getAllEstateWithPictures(): LiveData<List<EstateAllPictures>> {
        return estateRepository.getAllEstateWithPictures()
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