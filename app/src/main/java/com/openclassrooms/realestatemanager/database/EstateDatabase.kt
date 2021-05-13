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

            val startTime1: Long = Calendar.getInstance().timeInMillis

            estateDao.insert(
                Estate(
                    startTime = startTime1,
                    endTime = null,
                    estateTypeId = 3,
                    estatePrice = 450000,
                    employeeId = 1,
                    estateCity = "WASHINGTON",
                    estateDescription = "A nice house near of the Loire and surrounded by a peaceful municipal garden.",
                    estateSurface = 250,
                    estateRooms = 5,
                    estateStreet = "Pennsylvania Avenue NW",
                    estateStreetNumber = 1600,
                    estateCityPostalCode = "DC 20500",
                    estatePois = "1|5"
                )
            )

            pictureDao.insert(
                Picture(
                    url = "https://www.whitehouse.gov/wp-content/uploads/2021/01/about_the_white_house.jpg",
                    displayName = "Front",
                    estateId = startTime1,
                    orderNumber = 1
                )
            )

            pictureDao.insert(
                Picture(
                    url = "https://www.whitehouse.gov/wp-content/uploads/2021/01/white_house_building_wide.jpg",
                    displayName = "Aerial view",
                    estateId = startTime1,
                    orderNumber = 2
                )
            )

            val startTime2: Long = Calendar.getInstance().timeInMillis

            estateDao.insert(
                Estate(
                    startTime = startTime2,
                    endTime = null,
                    estateTypeId = 3,
                    estatePrice = 350000,
                    employeeId = 1,
                    estateCity = "SEATTLE",
                    estateDescription = "A nice house near of the Loire and surrounded by a peaceful municipal garden.",
                    estateSurface = 100,
                    estateRooms = 1,
                    estateStreet = "Broad St",
                    estateStreetNumber = 400,
                    estateCityPostalCode = "WA 98109",
                    estatePois = "777|911|1|3"
                )
            )

            pictureDao.insert(
                Picture(
                    url = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Space_Needle_2011-07-04.jpg/1200px-Space_Needle_2011-07-04.jpg",
                    displayName = "Aerial view",
                    estateId = startTime2,
                    orderNumber = 1
                )
            )

            pictureDao.insert(
                Picture(
                    url = "https://upload.wikimedia.org/wikipedia/commons/b/b9/Seattle_Space_needle.jpg",
                    displayName = "Street view",
                    estateId = startTime2,
                    orderNumber = 2
                )
            )

            Log.i("EstateDatabase", "Database populated")
        }


    }
}

