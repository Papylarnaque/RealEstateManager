package com.openclassrooms.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.database.EstateDatabase
import com.openclassrooms.realestatemanager.database.dao.*
import com.openclassrooms.realestatemanager.database.model.Employee
import com.openclassrooms.realestatemanager.database.model.Estate
import com.openclassrooms.realestatemanager.database.model.Poi
import com.openclassrooms.realestatemanager.database.model.Type
import com.openclassrooms.realestatemanager.provider.ContentProvider
import com.openclassrooms.realestatemanager.repository.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.io.IOException

class ContentProviderTest {

    private lateinit var contentResolver: ContentResolver
    private val id = 1L

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
    private val e1 = Estate(
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
    private val e2 = Estate(
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
    fun setUp() = runBlocking {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
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
        contentResolver = context.contentResolver
    }

    private fun initDB() = runBlocking {
        // INSERT DATA
        employeeDao.insert(
            Employee(
                employeeFirstName = "Joseph", employeeLastName = "PARRY", employeeId = 1
            )
        )
        employeeDao.insert(
            Employee(
                employeeFirstName = "Quinton", employeeLastName = "SPENCER", employeeId = 2
            )
        )
        employeeDao.insert(
            Employee(
                employeeFirstName = "Quinn", employeeLastName = "LYNCH", employeeId = 3
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

        estateDao.insert(e1)
        estateDao.insert(e2)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    @Test
    fun checkCursorNotNull() {
        val cursor = contentResolver.query(
            ContentUris.withAppendedId(
                ContentProvider.uri,
                id
            ),
            null,
            null,
            null,
            null
        )
        assertNotNull(cursor)
    }


    @Test
    fun checkCursorGetsEstates() {
        val c = contentResolver.query(
            ContentUris.withAppendedId(
                ContentProvider.uri,
                id
            ),
            null,
            null,
            null,
            null
        )

        c?.moveToFirst()
        assertEquals(c?.getLong(c.getColumnIndex("start_time_milli")), e1.startTime)
        assertEquals(c?.getLongOrNull(c.getColumnIndex("end_time_milli")), e1.endTime)
        assertEquals(c?.getInt(c.getColumnIndex("type_id")), e1.estateTypeId)
        assertEquals(c?.getInt(c.getColumnIndex("price")), e1.estatePrice)
        assertEquals(c?.getInt(c.getColumnIndex("surface")), e1.estateSurface)
        assertEquals(c?.getInt(c.getColumnIndex("rooms_count")), e1.estateRooms)
        assertEquals(c?.getString(c.getColumnIndex("description")), e1.estateDescription)
        assertEquals(c?.getString(c.getColumnIndex("street")), e1.estateStreet)
        assertEquals(c?.getInt(c.getColumnIndex("street_number")), e1.estateStreetNumber)
        assertEquals(c?.getString(c.getColumnIndex("city")), e1.estateCity)
        assertEquals(c?.getStringOrNull(c.getColumnIndex("postal_code")), e1.estateCityPostalCode)
        assertEquals(c?.getInt(c.getColumnIndex("employee_id")), e1.employeeId)

        c?.moveToNext()
        assertEquals(c?.getLong(c.getColumnIndex("start_time_milli")), e2.startTime)
        assertEquals(c?.getLongOrNull(c.getColumnIndex("end_time_milli")), e2.endTime)
        assertEquals(c?.getInt(c.getColumnIndex("type_id")), e2.estateTypeId)
        assertEquals(c?.getInt(c.getColumnIndex("price")), e2.estatePrice)
        assertEquals(c?.getInt(c.getColumnIndex("surface")), e2.estateSurface)
        assertEquals(c?.getInt(c.getColumnIndex("rooms_count")), e2.estateRooms)
        assertEquals(c?.getString(c.getColumnIndex("description")), e2.estateDescription)
        assertEquals(c?.getString(c.getColumnIndex("street")), e2.estateStreet)
        assertEquals(c?.getInt(c.getColumnIndex("street_number")), e2.estateStreetNumber)
        assertEquals(c?.getString(c.getColumnIndex("city")), e2.estateCity)
        assertEquals(c?.getStringOrNull(c.getColumnIndex("postal_code")), e2.estateCityPostalCode)
        assertEquals(c?.getInt(c.getColumnIndex("employee_id")), e2.employeeId)
        c?.close()
    }


}
