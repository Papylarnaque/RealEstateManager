package com.openclassrooms.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.database.EstateDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class ContentProviderTest {

    private lateinit var estateDatabase: EstateDatabase
    private lateinit var contentResolver: ContentResolver
    private val id = 1L

    @Before
    fun setUp() = runBlocking {
        estateDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            EstateDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @Test
    fun testQuery() {
        val cursor = contentResolver.query(
            ContentUris.withAppendedId(com.openclassrooms.realestatemanager.database.provider.ContentProvider.uri, id),
            null,
            null,
            null,
            null
        )
        assertNotNull(cursor)
        cursor?.getColumnIndexOrThrow("start_time_milli")
        cursor?.getColumnIndexOrThrow("end_time_milli")
        cursor?.getColumnIndexOrThrow("type_id")
        cursor?.getColumnIndexOrThrow("price")
        cursor?.getColumnIndexOrThrow("surface")
        cursor?.getColumnIndexOrThrow("rooms_count")
        cursor?.getColumnIndexOrThrow("description")
        cursor?.getColumnIndexOrThrow("street")
        cursor?.getColumnIndexOrThrow("street_number")
        cursor?.getColumnIndexOrThrow("city")
        cursor?.getColumnIndexOrThrow("postal_code")
        cursor?.getColumnIndexOrThrow("employee_id")
        cursor?.getColumnIndexOrThrow("poi_id")
        cursor?.close()
    }
}
