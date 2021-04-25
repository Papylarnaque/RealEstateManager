package com.openclassrooms.realestatemanager.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.database.EstateDatabase.Companion.getDatabase
import com.openclassrooms.realestatemanager.database.model.*
import com.openclassrooms.realestatemanager.repository.EmployeeRepository
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

    private val estateDao = getDatabase(application, viewModelScope).estateDao()
    private val estateRepository: EstateRepository = EstateRepository(estateDao)

    private val pictureDao = getDatabase(application, viewModelScope).pictureDao()
    private val pictureRepository: PictureRepository = PictureRepository(pictureDao)

    private val typeDao = getDatabase(application, viewModelScope).typeDao()
    private val typeRepository: TypeRepository = TypeRepository(typeDao)

    private val employeeDao = getDatabase(application, viewModelScope).employeeDao()
    private val employeeRepository: EmployeeRepository = EmployeeRepository(employeeDao)

    private val imagesFolder: File by lazy { getImagesFolder(getApplication()) }
    private val context: Context
        get() = getApplication()

    // TODO() Manage saving pictures

    //--------------- CREATION & EDITION ------------------//

    fun saveEstate(editMode: Boolean, estate: Estate, listPicture: List<Picture>) {
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
        savePictures(listPicture)
        onEstateUpdated(estate)
    }


    private fun savePictures(listPicture: List<Picture>) {
        for (picture in listPicture) {
            if (!picture.url.contains(imagesFolder.path, true))
                picture.url = copyImageFromUriToAppFolder(picture.url.toUri())
            viewModelScope.launch { pictureRepository.insert(picture) }
        }
    }


//--------------- EDIT MODE FUNCTIONS ------------------//

    fun getEstateWithId(estateKey: Long): LiveData<DetailedEstate> {
        return estateRepository.getEstate(estateKey)
    }

    fun getEstatePictures(estateKey: Long): LiveData<List<Picture>> {
        return pictureRepository.getEstatePictures(estateKey)
    }

//------------------------------------------------------//

    fun allTypes(): LiveData<List<Type>> = typeRepository.allTypes
    fun allEmployees(): LiveData<List<Employee>> = employeeRepository.allEmployees


//----------------- MANAGE PICTURES --------------------//

    private fun getImagesFolder(context: Context): File {
        return File(context.filesDir, "images/").also {
            if (!it.exists()) {
                it.mkdir()
            }
        }
    }

    fun saveImageFromCamera(bitmap: Bitmap): String {
        val imageFile = File(imagesFolder, generateFilename(Source.CAMERA))
        val imageStream = FileOutputStream(imageFile)

        Log.i("TakePicture", "$imageFile")

        compressPicture(bitmap, imageStream)


//        sharePictureURI(imageFile.path)
        return imageFile.path
    }

    private fun compressPicture(bitmap: Bitmap, imageStream: FileOutputStream) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageStream)
                } catch (e: Exception) {
                    Log.e(javaClass.simpleName, "Error writing bitmap", e)
                }
            }
        }
    }

    private fun copyImageFromUriToAppFolder(uri: Uri): String {
        val imageFile = File(imagesFolder, generateFilename(Source.PICKER))
        val imageStream = FileOutputStream(imageFile)

        context.contentResolver.openInputStream(uri)?.let {
            // TODO() compress picture copied to app folder ?
//            compressPicture(BitmapFactory.decodeStream(it), imageStream)
            copyImageFromStream(it, imageStream)
        }
        return imageFile.toString()
    }


//------------------ NOTIFICATIONS ---------------------//

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