package com.openclassrooms.realestatemanager.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.database.Estate
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.repository.EstateRepository
import com.openclassrooms.realestatemanager.utils.Source
import com.openclassrooms.realestatemanager.utils.applyGrayscaleFilter
import com.openclassrooms.realestatemanager.utils.generateFilename
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private const val FILEPATH_XML_KEY = "files-path"
private const val RANDOM_IMAGE_URL = "https://source.unsplash.com/random/500x500"
val ACCEPTED_MIMETYPES = arrayOf("image/jpeg", "image/png")

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
        estatePicture: String?,
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
            pictureUrl = estatePicture,
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
            // Availability to true by default => endTimeMilli == null
            endTime = null
        )


        repository.insert(estate)
        Log.i(
            "CreationViewModel", "added a new estate of type ${estate.estateType}" +
                    "with id ${estate.startTime}"
        )
    }

    fun getEstateWithId(estateKey: Long): LiveData<Estate> {
        return repository.getEstate(estateKey)
    }

    suspend fun updateEstate(
        estateStartTime: Long,
        estatePicture: String?,
        estateType: String,
        estateDescription: String,
        estatePrice: Int,
        estateSurface: Int?,
        estateRooms: Int?,
        estateStreet: String,
        estateStreetNumber: Int?,
        estatePostalCode: String?,
        estateCity: String,
        estateEmployee: String,
        endTimeMilli: Long?
    ) {

        // store data in a new estate to monitor the estateId
        val estate = Estate(
            startTime = estateStartTime,
            pictureUrl = estatePicture,
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
            endTime = endTimeMilli
        )


        repository.update(estate)
        Log.i(
            "CreationViewModel", "added a new estate of type ${estate.estateType}" +
                    "with id ${estate.startTime} with price ${estate.estatePrice}"
        )
    }

    private val imagesFolder: File by lazy { getImagesFolder(getApplication()) }

    private fun getImagesFolder(context: Context): File {

        return File(context.filesDir, "images/").also {
            if (!it.exists()) {
                it.mkdir()
            }
        }
    }

    fun saveImageFromCamera(bitmap: Bitmap) {
        val imageFile = File(imagesFolder, generateFilename(Source.CAMERA))
        val imageStream = FileOutputStream(imageFile)

        Log.i("TakePicture", "$imageFile")

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val imageBitmap = withContext(Dispatchers.Default) {
                        applyGrayscaleFilter(bitmap)
                    }
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageStream)
                    imageStream.flush()
                    imageStream.close()

                } catch (e: Exception) {
                    Log.e(javaClass.simpleName, "Error writing bitmap", e)
                }
            }
        }

        sharePictureURI(imageFile.absolutePath)
    }

    /**
     * Navigation for the EstateListDetail fragment.
     */
    private val _imageURL = MutableLiveData<String>()
    val imageURL
        get() = _imageURL

    fun sharePictureURI(url: String) {
        _imageURL.value = url
    }


}