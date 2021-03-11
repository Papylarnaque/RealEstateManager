package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.openclassrooms.realestatemanager.database.Estate
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.EstateDatabaseDao
import com.openclassrooms.realestatemanager.database.Picture
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import java.util.*

@RunWith(RobolectricTestRunner::class)
class EstateDatabaseTest {

    private lateinit var estateDao: EstateDatabaseDao
    private lateinit var db: EstateDatabase

    private val estate1 = Estate(
        startTimeMilli = Calendar.getInstance().timeInMillis,
        endTimeMilli = null,
        estateType = "House",
        estatePrice = 1000,
        estateEmployee = "Etienne"
    )

    private val startTime : Long = 99999999

    private val estate2 = Estate(
        startTimeMilli = startTime,
        endTimeMilli = null,
        estateType = "Flat",
        estatePrice = 1000000,
        estateEmployee = "Etienne"
    )

    private val picture21 = Picture(
        start_time_milli = estate2.startTimeMilli,
        pictureUrl = "TestURLpicture2_1"
    )

    private val picture22 = Picture(
        start_time_milli = estate2.startTimeMilli,
        pictureUrl = "TestURLpicture2_2"
    )



    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, EstateDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        estateDao = db.estateDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    @Test
    @Throws(Exception::class)
    fun insertAndGetEstate() = runBlocking {
        estateDao.insertEstate(estate1)
        val estateList : List<Estate>? = estateDao.getAllEstates().value
        if (estateList != null) {
            assert(estateList.contains(estate1) )
        } else assert(false)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetSpecificEstate() = runBlocking {
        estateDao.insertEstate(estate1)
        estateDao.insertEstate(estate2)

        val estate : Estate? = estateDao.getEstate(startTime)
        if (estate != null) {
            assert(estate == estate2)
        } else assert(false)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndUpdateThenGetSpecificEstate() = runBlocking {
        estateDao.insertEstate(estate1)
        estateDao.insertEstate(estate2)

        val newEstate = Estate(
            startTimeMilli = startTime,
            endTimeMilli = null,
            estateType = "Flat",
            estatePrice = 1000000,
            estateEmployee = "Etienne",
            estateDescription = "Magnificent flat",
            estateAvailability = false
        )

        estateDao.updateEstate(newEstate)

        val estate : Estate? = estateDao.getEstate(startTime)

        if (estate != null) {
            assert(estate != estate2)
            assert(estate == newEstate)
        } else assert(false)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDeleteEstate() = runBlocking {

        estateDao.insertEstate(estate1)
        estateDao.insertEstate(estate2)

        estateDao.deleteEstate(startTime)

        val estateList : List<Estate>? = estateDao.getAllEstates().value
        if (estateList != null) {
            assert(!estateList.contains(estate2) )
            assert(estateList.size==1)
        } else assert(false)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPictures() = runBlocking {

        estateDao.insertEstate(estate2)
        estateDao.insertPicture(picture21)

        val url = estateDao.getPictures(estate2.startTimeMilli).value

        if (url != null) {
            assert(url.contains(picture21.pictureUrl))
            assert(url.contains(picture22.pictureUrl))
        } else assert(false)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPicturesThenDeletePicture() = runBlocking {

        estateDao.insertEstate(estate2)
        estateDao.insertPicture(picture21)
        estateDao.insertPicture(picture22)

        val url = estateDao.getPictures(estate2.startTimeMilli).value

        estateDao.deletePicture(estate2.startTimeMilli, picture21.pictureUrl)

        if (url != null) {
            assert(!url.contains(picture21.pictureUrl))
            assert(url.contains(picture22.pictureUrl))
        } else assert(false)
    }

}
