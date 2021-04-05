package com.openclassrooms.realestatemanager.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Database(entities = [Estate::class], version = 1, exportSchema = false)
abstract class EstateDatabase : RoomDatabase() {

    abstract fun estateDatabaseDao(): EstateDatabaseDao

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
                        // Migration is not part of this codelab.
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
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.estateDatabaseDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         */
        suspend fun populateDatabase(estateDatabaseDao: EstateDatabaseDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            estateDatabaseDao.deleteAll()

            val estate1 = Estate(
                    startTimeMilli = Calendar.getInstance().timeInMillis,
                    endTimeMilli = null,
                    estateType = "House",
                    estatePrice = 100000,
                    estateEmployee = "Etienne",
                    estateCity = "Nantes",
                    pictureUrl = "content://com.android.externalstorage.documents/" +
                            "document/" + // Hardcoded URI of image in a smartphone
                            "primary%3ADownload%2FMjAyMTAyNjZmNTViNDZhYjQ2YzFmZTE5NGJmMDM1NTZmZWZiNTY.jpeg",
                    estateDescription = "A nice house near of the Loire and surrounded by a peaceful municipal garden.",
                    estateSurface = 250,
                    estateAvailability = true,
                    estateRooms = 5,
                    estateStreet = "rue des Ponts"
            )

            estateDatabaseDao.insert(estate1)

            Log.i("EstateDatabase", "populateDatabase with estate1")
        }


    }
}

