package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.openclassrooms.realestatemanager.database.Estate
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.EstateDatabaseDao
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
            estatePrice = 650000,
            estateEmployee = "Etienne",
            pictureUrl = "TestURLpicture1_1",
            estateCity = "Nantes",
            estateCityPostalCode = 44000,
            estateStreetNumber = 15,
            estateStreet = "rue de Paris",
            estateRooms = 8,
            estateSurface = 320,
            estateDescription = "Nice house in the heart of Nantes"
    )

    private val startTime: Long = 99999999

    private val estate2 = Estate(
            startTimeMilli = startTime,
            endTimeMilli = null,
            estateType = "Flat",
            estatePrice = 1000000,
            estateEmployee = "Etienne",
            pictureUrl = "TestURLpicture2_1",
            estateCity = "Lille",
            estateCityPostalCode = 59000,
            estateStreetNumber = 23,
            estateStreet = "rue de Paris",
            estateRooms = 10,
            estateSurface = 400,
            estateDescription = "Nice flat in the heart of Lille"
    )


    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, EstateDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        estateDao = db.estateDatabaseDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    @Test
    @Throws(Exception::class)
    fun insertAndGetEstate() = runBlocking {
        estateDao.insert(estate1)
        val estateList: List<Estate>? = estateDao.getAllEstates().value
        if (estateList != null) {
            assert(estateList.contains(estate1))
        } else assert(false)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetSpecificEstate() = runBlocking {
        estateDao.insert(estate1)
        estateDao.insert(estate2)

        assert(estateDao.getEstate(startTime).value == estate2)

    }

    @Test
    @Throws(Exception::class)
    fun insertAndUpdateThenGetSpecificEstate() = runBlocking {
        estateDao.insert(estate1)
        estateDao.insert(estate2)

        val newEstate = Estate(
                startTimeMilli = startTime,
                endTimeMilli = null,
                estateType = "Flat",
                estatePrice = 1000000,
                estateEmployee = "Etienne",
                estateDescription = "Magnificent flat",
                estateAvailability = false,
                pictureUrl = "TestURLpicture_1",
                estateCity = "Nantes",
                estateCityPostalCode = 44000,
                estateStreetNumber = 15,
                estateStreet = "rue de Paris",
                estateRooms = 8,
                estateSurface = 320
        )

        estateDao.updateEstate(newEstate)

        assert(estateDao.getEstate(startTime).value != estate2)
        assert(estateDao.getEstate(startTime).value == newEstate)

    }


}
