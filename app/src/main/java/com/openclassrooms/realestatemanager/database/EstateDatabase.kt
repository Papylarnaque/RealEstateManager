package com.openclassrooms.realestatemanager.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.database.dao.*
import com.openclassrooms.realestatemanager.database.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Database(
    entities = [Estate::class, Poi::class, Employee::class, Type::class, Picture::class],
    version = 2, exportSchema = false
)
abstract class EstateDatabase : RoomDatabase() {

    abstract fun estateDao(): EstateDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun poiDao(): PoiDao
    abstract fun typeDao(): TypeDao
    abstract fun pictureDao(): PictureDao

    companion object {
        // This will help to avoid repeatedly initializing the database
        @Volatile
        private var INSTANCE: EstateDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): EstateDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EstateDatabase::class.java,
                    "estate_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    .fallbackToDestructiveMigration()
                    .addCallback(EstateDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }


        private class EstateDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Start the app with a clean database at each creation
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(
                            database.estateDao(),
                            database.employeeDao(),
                            database.poiDao(),
                            database.typeDao(),
                            database.pictureDao(),
                        )
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         */
        suspend fun populateDatabase(
            estateDao: EstateDao,
            employeeDao: EmployeeDao,
            poiDao: PoiDao,
            typeDao: TypeDao,
            pictureDao: PictureDao
        ) {
            // CLEAN DB
            estateDao.deleteAll()
            employeeDao.deleteAll()
            poiDao.deleteAll()
            typeDao.deleteAll()
            pictureDao.deleteAll()

            // INSERT DATA
            employeeDao.insert(
                Employee(
                    employeeFirstName = "Etienne", employeeLastName = "DOC", employeeId = 1
                )
            )
            employeeDao.insert(
                Employee(
                    employeeFirstName = "Aurélie", employeeLastName = "ROC", employeeId = 2
                )
            )

            typeDao.insert(Type(typeId = 1, typeName = "Duplex"))
            typeDao.insert(Type(typeId = 2, typeName = "Loft"))
            typeDao.insert(Type(typeId = 3, typeName = "Manor"))
            typeDao.insert(Type(typeId = 4, typeName = "Penthouse"))

            poiDao.insert(Poi(poiId = 1, poiName = "Town hall"))
            poiDao.insert(Poi(poiId = 2, poiName = "Shop"))
            poiDao.insert(Poi(poiId = 3, poiName = "Primary School"))
            poiDao.insert(Poi(poiId = 911, poiName = "Police Station"))
            poiDao.insert(Poi(poiId = 1000, poiName = "Pharmacy"))
            poiDao.insert(Poi(poiId = 4, poiName = "Municipal Garden"))
            poiDao.insert(Poi(poiId = 5, poiName = "Restaurants"))
            poiDao.insert(Poi(poiId = 777, poiName = "Airport"))

            val startTime1: Long = 1615760275000 // Sunday 14 March 2021
            estateDao.insert(
                Estate(
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
                    estateCityPostalCode = "",
                    estatePois = "1|5"
                )
            )
            pictureDao.insert(
                Picture(
                    url = "https://thumbs.cityrealty.com/fit-in/x340/5/c6/2141285/15-Central-Park-West-a5967e4d46440c42c6c6efd692a2b1cf-24733632645c94789d8fa3bdd721aa19.jpg",
                    displayName = "",
                    estateId = startTime1,
                    orderNumber = 1
                )
            )
            pictureDao.insert(
                Picture(
                    url = "https://thumbs.cityrealty.com/fit-in/x340/5/c6/2141285/15-Central-Park-West-90af8946b03137fe5feefdbfaaf0ecc0-495202d8403dbd449273d6b01b59fc60.jpg",
                    displayName = "",
                    estateId = startTime1,
                    orderNumber = 2
                )
            )
            pictureDao.insert(
                Picture(
                    url = "https://thumbs.cityrealty.com/fit-in/x340/5/c6/2141285/15-Central-Park-West-cba149cd3294a3c34d1f9c2318ba6fa4-3c947d12aca2a63cdb8c2f32fc0ba8c3.jpg",
                    displayName = "Bedroom",
                    estateId = startTime1,
                    orderNumber = 3
                )
            )
            pictureDao.insert(
                Picture(
                    url = "https://thumbs.cityrealty.com/fit-in/x340/5/c6/2141285/15-Central-Park-West-b45100247b4f59c27c85fb946e4b3676-228ee9fd6423ec8d0f2c98ea011afbae.jpg",
                    displayName = "Bathroom",
                    estateId = startTime1,
                    orderNumber = 4
                )
            )
            pictureDao.insert(
                Picture(
                    url = "https://thumbs.cityrealty.com/fit-in/x340/5/c6/2141285/15-Central-Park-West-8e0dbea5f9baf228e688678674302a9a-cd4abf3f0b487b8913ae2b2e476e15f3.jpg",
                    displayName = "Balcony",
                    estateId = startTime1,
                    orderNumber = 5
                )
            )

            val startTime2: Long = Calendar.getInstance().timeInMillis
            estateDao.insert(
                Estate(
                    startTime = startTime2,
                    endTime = null,
                    estateTypeId = 3,
                    estatePrice = 12500000,
                    employeeId = 1,
                    estateCity = "NEW-YORK",
                    estateDescription = "A Fifth Avenue Masterpiece with open Central Park and Reservoir Views, this sprawling 12 room Pre-War Cooperative is pristine and ready to move in due to a recent full renovation by Ferguson Shamamian. The interiors were meticulously curated by the world-renowned designer, Bunny Williams, the distinguished expert in creating homes that are as grand and stylish as they are comfortable and serene. This residence is far from the exception, featuring finest quality finishes throughout and an atmosphere of sophistication that can be compared to nothing else available. Stepping into the private jewel box 14th floor elevator landing, one is welcomed by spectacular light bouncing from the glossy custom patinaed Venetian Plaster adorning the walls of a double-wide Gallery and pouring through to illuminate the rest of the open yet classic floorplan.",
                    estateSurface = 100,
                    estateRooms = 1,
                    estateStreet = "Fifth Avenue",
                    estateStreetNumber = 1158,
                    estateCityPostalCode = "WA 98109",
                    estatePois = "777|911|1|3"
                )
            )
            pictureDao.insert(
                Picture(
                    url = "https://thumbs.cityrealty.com/fit-in/x340/6/73/2398006/1158-Fifth-Avenue-63c30dd74d1255ae9af2cb8dc9f85c8d-794949be90de93955f20ab44c55af7c3.jpg",
                    displayName = "Outside view",
                    estateId = startTime2,
                    orderNumber = 1
                )
            )
            pictureDao.insert(
                Picture(
                    url = "https://thumbs.cityrealty.com/fit-in/x340/6/73/2398006/1158-Fifth-Avenue-84a8f5bdf3b3fdc737e545e71ca0deb4-8ad080f92a8aa6d9665af9d30a7712e5.jpg",
                    displayName = "Kitchen",
                    estateId = startTime2,
                    orderNumber = 2
                )
            )

            val startTime3: Long = Calendar.getInstance().timeInMillis
            estateDao.insert(
                Estate(
                    startTime = startTime3,
                    endTime = null,
                    estateTypeId = 4,
                    estatePrice = 1500000,
                    employeeId = 1,
                    estateCity = "NEW-YORK",
                    estateDescription = "A nice residence in the heart of Manhattan. " +
                            "Located at the 50th floor",
                    estateSurface = 200,
                    estateRooms = 8,
                    estateStreet = "Park Avenue",
                    estateStreetNumber = 432,
                    estateCityPostalCode = null,
                    estatePois = "777|911|1|3"
                )
            )
            pictureDao.insert(
                Picture(
                    url = "https://www.432parkavenue.com/assets/imgs/content/residences/1.jpg",
                    displayName = "Building view",
                    estateId = startTime3,
                    orderNumber = 1
                )
            )
            pictureDao.insert(
                Picture(
                    url = "https://www.432parkavenue.com/assets/imgs/content/views/432_View_01.jpg",
                    displayName = "Outside view",
                    estateId = startTime3,
                    orderNumber = 2
                )
            )
            pictureDao.insert(
                Picture(
                    url = "https://www.432parkavenue.com/assets/imgs/content/residences/2.jpg",
                    displayName = "Inside view",
                    estateId = startTime3,
                    orderNumber = 3
                )
            )


            Log.i("EstateDatabase", "Database populated")
        }


    }
}

