package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.dao.*
import com.openclassrooms.realestatemanager.database.model.Employee
import com.openclassrooms.realestatemanager.database.model.Estate
import com.openclassrooms.realestatemanager.database.model.Poi
import com.openclassrooms.realestatemanager.database.model.Type
import com.openclassrooms.realestatemanager.repository.*
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

    private lateinit var db: EstateDatabase
    private lateinit var estateDao: EstateDao
    private lateinit var employeeDao: EmployeeDao
    private lateinit var pictureDao: PictureDao
    private lateinit var poiDao: PoiDao
    private lateinit var typeDao: TypeDao
    private lateinit var estateRepository: EstateRepository
    private lateinit var pictureRepository: PictureRepository
    private lateinit var typeRepository: TypeRepository
    private lateinit var employeeRepository: EmployeeRepository
    private lateinit var poiRepository: PoiRepository

    private val startTime2: Long = 99999999
    private val estate1 = Estate(
        startTime = Calendar.getInstance().timeInMillis,
        endTime = null,
        estateTypeId = 1,
        estatePrice = 650000,
        employeeId = 1,
        estateCity = "Nantes",
        estateCityPostalCode = "44000",
        estateStreetNumber = 15,
        estateStreet = "rue de Paris",
        estateRooms = 8,
        estateSurface = 320,
        estateDescription = "Nice house in the heart of Nantes",
        estatePois = "1"
    )
    private val estate2 = Estate(
        startTime = startTime2,
        endTime = null,
        estateTypeId = 2,
        estatePrice = 1000000,
        employeeId = 2,
        estateCity = "Lille",
        estateCityPostalCode = "59000",
        estateStreetNumber = 23,
        estateStreet = "rue de Paris",
        estateRooms = 10,
        estateSurface = 400,
        estateDescription = "Nice flat in the heart of Lille",
        estatePois = "911"
    )


    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, EstateDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()

        poiDao = db.poiDao()
        typeDao = db.typeDao()
        employeeDao = db.employeeDao()
        estateDao = db.estateDao()
        pictureDao = db.pictureDao()

        estateRepository = EstateRepository(estateDao)
        pictureRepository = PictureRepository(pictureDao)
        typeRepository = TypeRepository(typeDao)
        employeeRepository = EmployeeRepository(employeeDao)
        poiRepository = PoiRepository(poiDao)


        // TODO Fix Test with updated DB
        initDB()
    }

    private fun initDB() = runBlocking {
        // INSERT DATA
        employeeRepository.insert(
            Employee(
                employeeFirstName = "Etienne", employeeLastName = "DESCAMPS", employeeId = 1
            )
        )
        employeeRepository.insert(
            Employee(
                employeeFirstName = "Aurélie", employeeLastName = "RAYMOND", employeeId = 2
            )
        )

        typeRepository.insert(Type(typeId = 1, typeName = "Duplex"))
        typeRepository.insert(Type(typeId = 2, typeName = "Loft"))
        typeRepository.insert(Type(typeId = 3, typeName = "Manor"))
        typeRepository.insert(Type(typeId = 4, typeName = "Penthouse"))

        poiRepository.insert(Poi(poiId = 1, poiName = "Town hall"))
        poiRepository.insert(Poi(poiId = 2, poiName = "Shop"))
        poiRepository.insert(Poi(poiId = 3, poiName = "Primary School"))
        poiRepository.insert(Poi(poiId = 911, poiName = "Police Station"))
        poiRepository.insert(Poi(poiId = 1000, poiName = "Pharmacy"))
        poiRepository.insert(Poi(poiId = 4, poiName = "Municipal Garden"))
        poiRepository.insert(Poi(poiId = 5, poiName = "Restaurants"))
        poiRepository.insert(Poi(poiId = 777, poiName = "Airport"))
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

        assert(estateDao.getEstate(startTime2).value?.equals(estate2) == true)

    }

    @Test
    @Throws(Exception::class)
    fun insertAndUpdateThenGetSpecificEstate() = runBlocking {
        estateDao.insert(estate1)
        estateDao.insert(estate2)

        val newEstate = Estate(
            startTime = startTime2,
            endTime = null,
            estateTypeId = 2,
            estatePrice = 1000000,
            employeeId = 1,
            estateDescription = "Magnificent flat",
            estateCity = "Nantes",
            estateCityPostalCode = "44000",
            estateStreetNumber = 15,
            estateStreet = "rue de Paris",
            estateRooms = 8,
            estateSurface = 320,
            estatePois = "91  1"
        )

        estateDao.updateEstate(newEstate)

        assert(estateDao.getEstate(startTime2).value?.equals(estate2) == true)
        assert(estateDao.getEstate(startTime2).value?.equals(newEstate) == true)

    }


}
