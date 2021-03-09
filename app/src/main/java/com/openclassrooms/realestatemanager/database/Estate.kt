package com.openclassrooms.realestatemanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "estate_table")
data class Estate(
        @PrimaryKey(autoGenerate = true)
        val estateId: Long = 0L,

        @ColumnInfo(name = "start_time_milli")
        val startTimeMilli: Long = System.currentTimeMillis(),

        @ColumnInfo(name = "end_time_milli")
        val endTimeMilli: Long? = startTimeMilli,

        @ColumnInfo(name = "type")
        val estateType: String = "Maison",

        @ColumnInfo(name = "price")
        val estatePrice: Int = 0,

        @ColumnInfo(name = "surface")
        val estateSurface: Int = 0,

        @ColumnInfo(name = "rooms_count")
        val estateRooms: Int = 0,

        @ColumnInfo(name = "description")
        val estateDescription: String = "",

//        @ColumnInfo(name = "picture_url")
//        val estateUrlPicture: List<String>,
//
//        @ColumnInfo(name = "address")
//        val estateAddress: Address,
//
//        @ColumnInfo(name = "nearby_places")
//        val estateNearby: List<String>,

        @ColumnInfo(name = "nearby_places")
        val estateAvailability: Boolean = true,

        @ColumnInfo(name = "employee")
        val estateEmployee: String,

)

