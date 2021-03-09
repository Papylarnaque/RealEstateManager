package com.openclassrooms.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Estate::class], version = 1, exportSchema = false)
abstract class EstateDatabase : RoomDatabase() {

    abstract val estateDatabaseDao: EstateDatabaseDao

    companion object {
        // This will help to avoid repeatedly initializing the database
        @Volatile
        private var INSTANCE: EstateDatabase? = null

        /**
         * Helper function to get the database.
         * @param context application context Singleton used to get access to the filesystem.
         */
        fun getInstance(context: Context): EstateDatabase {
            // ensure we only initialize db once
            synchronized(this) {
                // Copy the current value of INSTANCE to a local variable
                var instance = INSTANCE
                // If instance is 'null' make a new database instance.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            EstateDatabase::class.java,
                            "estate_database"
                    )
                            // Wipes and rebuilds instead of migrating if no Migration object.
                            .fallbackToDestructiveMigration()
                            .build()
                    // Assign INSTANCE to the newly created database.
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}
