package com.openclassrooms.realestatemanager.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.model.Estate
import com.openclassrooms.realestatemanager.database.model.Picture
import com.openclassrooms.realestatemanager.database.model.Type
import com.openclassrooms.realestatemanager.repository.EstateRepository
import com.openclassrooms.realestatemanager.repository.PictureRepository
import com.openclassrooms.realestatemanager.repository.TypeRepository
import com.openclassrooms.realestatemanager.utils.Source
import com.openclassrooms.realestatemanager.utils.copyImageFromStream
import com.openclassrooms.realestatemanager.utils.generateFilename
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class CreationViewModel(application: Application) : AndroidViewModel(application) {

    private val estateRepository: EstateRepository
    private val pictureRepository: PictureRepository
    private val typeRepository: TypeRepository
    private val imagesFolder: File by lazy { getImagesFolder(getApplication()) }
    private val context: Context
        get() = getApplication()

    // TODO() Manage saving pictures

    init {
        with(EstateDatabase.getDatabase(application, viewModelScope)) {
            val estateDao = this.estateDao()
            estateRepository = EstateRepository(estateDao)

            val pictureDao = this.pictureDao()
            pictureRepository = PictureRepository(pictureDao)

            val typeDao = this.typeDao()
            typeRepository = TypeRepository(typeDao)
        }
    }

    //--------------- CREATION & EDITION ------------------//

    fun saveEstate(editMode: Boolean, estate: Estate) {
        if (editMode) {
            GlobalScope.launch {
                estateRepository.update(estate)
                Log.i(
                    "CreationViewModel",
                    "updated an existing estate with id ${estate.startTime}"
                )
            }
        } else {
            GlobalScope.launch {
                estateRepository.insert(estate)
                Log.i(
                    "CreationViewModel ",
                    "added a new estate with id ${estate.startTime}"
                )
            }
        }
//        savePictures()
        onEstateUpdated(estate)
    }


    private fun savePictures() {
        TODO("Not yet implemented")
    }


    //--------------- EDIT MODE FUNCTIONS ------------------//

    fun getEstateWithId(estateKey: Long): LiveData<Estate> {
        return estateRepository.getEstate(estateKey)
    }

    fun getEstatePictures(estateKey: Long): LiveData<List<Picture>> {
        return pictureRepository.getEstatePictures(estateKey)
    }

    //------------------------------------------------------//

    fun allTypes(): LiveData<List<Type>> = typeRepository.allTypes


    //----------------- MANAGE PICTURES --------------------//

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
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageStream)
                } catch (e: Exception) {
                    Log.e(javaClass.simpleName, "Error writing bitmap", e)
                }
            }
        }

        sharePictureURI(imageFile.absolutePath)
    }

    fun copyImageFromUriToAppFolder(uri: Uri) {
        val imageFile = File(imagesFolder, generateFilename(Source.PICKER))
        val imageStream = FileOutputStream(imageFile)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.let {
                    copyImageFromStream(it, imageStream)
                }
            }
        }
    }


    //------------------ NOTIFICATIONS ---------------------//

    /**
     * Share ImageURL with the Creationfragment
     */
    private val _imageURL = MutableLiveData<String>()
    val imageURL
        get() = _imageURL

    private fun sharePictureURI(url: String) {
        _imageURL.value = url
    }


    /**
     * Navigation notification
     */
    private val _navigateToEstateDetail = MutableLiveData<Estate>()
    val navigateToEstateDetail
        get() = _navigateToEstateDetail

    private fun onEstateUpdated(estate: Estate) {
        _navigateToEstateDetail.value = estate
    }


}