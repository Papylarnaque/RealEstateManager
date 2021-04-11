package com.openclassrooms.realestatemanager.viewmodel

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
        val estateDatabaseDao =
                EstateDatabase.getDatabase(application, viewModelScope).estateDatabaseDao()
        repository = EstateRepository(estateDatabaseDao)
        allEstates = repository.allEstates


    }


    suspend fun createNewEstate(
            documentUri: Uri?,
            estateType: String,
            estateDescription: String,
            estatePrice: Int,
            estateSurface: Int?,
            estateRooms: Int?,
            estateStreet: String,
            estateStreetNumber: Int?,
            estatePostalCode: String?,
            estateCity: String,
            estateEmployee: String
    ) {

        // store data in a new estate to monitor the estateId
        val estate = Estate(
                pictureUrl = documentUri.toString(),
                estateType = estateType,
                estateDescription = estateDescription,
                estatePrice = estatePrice,
                estateSurface = estateSurface,
                estateRooms = estateRooms,
                estateStreet = estateStreet,
                estateStreetNumber = estateStreetNumber,
                estateCityPostalCode = estatePostalCode,
                estateCity = estateCity,
                estateEmployee = estateEmployee,
                // Availability to true by default
                estateAvailability = true
        )


        repository.insert(estate)
        Log.i("CreationViewModel", "added a new estate of type ${estate.estateType}" +
                "with id ${estate.startTimeMilli}")
    }


    fun getEstateWithId(estateKey: Long): LiveData<Estate> {
        return repository.getEstate(estateKey)
    }

    suspend fun updateEstate(
            estateID: Long,
            documentUri: Uri?,
            estateType: String,
            estateDescription: String,
            estatePrice: Int,
            estateSurface: Int?,
            estateRooms: Int?,
            estateStreet: String,
            estateStreetNumber: Int?,
            estatePostalCode: String?,
            estateCity: String,
            estateEmployee: String
    ) {

        // store data in a new estate to monitor the estateId
        val estate = Estate(
                startTimeMilli = estateID,
                pictureUrl = documentUri.toString(),
                estateType = estateType,
                estateDescription = estateDescription,
                estatePrice = estatePrice,
                estateSurface = estateSurface,
                estateRooms = estateRooms,
                estateStreet = estateStreet,
                estateStreetNumber = estateStreetNumber,
                estateCityPostalCode = estatePostalCode,
                estateCity = estateCity,
                estateEmployee = estateEmployee,
                // Availability to true by default
                estateAvailability = true
        )


        repository.update(estate)
        Log.i("CreationViewModel", "added a new estate of type ${estate.estateType}" +
                "with id ${estate.startTimeMilli} with price ${estate.estatePrice}")
    }


}