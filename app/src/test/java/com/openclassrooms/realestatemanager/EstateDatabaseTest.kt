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

    private val startTime1: Long = 1615760275000 // Sunday 14 March 2021
    private val estate1 = Estate(
        startTime = startTime1,
        endTime = null,
        estateTypeId = 4,
        estatePrice = 5750000,
        employeeId = 1,
        estateCity = "NEW-YORK",
        estateDescription = "Apt. PH43 ",
        estateSurface = 250,
        estateRooms = 5,
        estateStreet = "Central Park West",
        estateStreetNumber = 15,
        estateCityPostalCode = null,
    )
    private val startTime2: Long = 1615860275000
    private val endTime2: Long = 1615960275000
    private val estate2 = Estate(
        startTime = startTime2,
        endTime = endTime2,
        estateTypeId = 3,
        estatePrice = 12500000,
        employeeId = 3,
        estateCity = "NEW-YORK",
        estateDescription = "A Fifth Avenue Masterpiece with open Central Park and Reservoir Views, this sprawling 12 room Pre-War Cooperative is pristine and ready to move in due to a recent full renovation by Ferguson Shamamian. The interiors were meticulously curated by the world-renowned designer, Bunny Williams, the distinguished expert in creating homes that are as grand and stylish as they are comfortable and serene. This residence is far from the exception, featuring finest quality finishes throughout and an atmosphere of sophistication that can be compared to nothing else available. Stepping into the private jewel box 14th floor elevator landing, one is welcomed by spectacular light bouncing from the glossy custom patinaed Venetian Plaster adorning the walls of a double-wide Gallery and pouring through to illuminate the rest of the open yet classic floorplan.",
        estateSurface = 300,
        estateRooms = 8,
        estateStreet = "Fifth Avenue",
        estateStreetNumber = 1158,
        estateCityPostalCode = "WA 98109",
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

        initDB()
    }

    private fun initDB() = runBlocking {
        // INSERT DATA
        employeeRepository.insert(
            Employee(
                employeeFirstName = "Joseph", employeeLastName = "PARRY", employeeId = 1
            )
        )
        employeeRepository.insert(
            Employee(
                employeeFirstName = "Quinton", employeeLastName = "SPENCER", employeeId = 2
            )
        )
        employeeRepository.insert(
            Employee(
                employeeFirstName = "Quinn", employeeLastName = "LYNCH", employeeId = 3
            )
        )
        employeeRepository.insert(
            Employee(
                employeeFirstName = "Randall", employeeLastName = "RAY", employeeId = 4
            )
        )
        employeeRepository.insert(
            Employee(
                employeeFirstName = "Ayaan", employeeLastName = "WHITNEY", employeeId = 5
            )
        )
        employeeRepository.insert(
            Employee(
                employeeFirstName = "Colt", employeeLastName = "ROBERTSON", employeeId = 6
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

        assert(estateDao.getEstate(startTime2).equals(estate2))

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
        )

        estateDao.updateEstate(newEstate)

        assert(estateDao.getEstate(startTime2).equals(estate2))
        assert(estateDao.getEstate(startTime2).equals(newEstate))

    }

}
