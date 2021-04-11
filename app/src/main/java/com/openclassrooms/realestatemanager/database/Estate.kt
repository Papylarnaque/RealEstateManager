package com.openclassrooms.realestatemanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "estate_table")
data class Estate(

        @PrimaryKey
        @ColumnInfo(name = "start_time_milli")
        val startTimeMilli: Long = System.currentTimeMillis(),

        @ColumnInfo(name = "end_time_milli")
        val endTimeMilli: Long? = startTimeMilli,

        @ColumnInfo(name = "type")
        var estateType: String,

        @ColumnInfo(name = "price")
        val estatePrice: Int?,

        @ColumnInfo(name = "surface")
        val estateSurface: Int?,

        @ColumnInfo(name = "rooms_count")
        val estateRooms: Int?,

        @ColumnInfo(name = "description")
        val estateDescription: String = "",

        @ColumnInfo(name = "street")
        val estateStreet: String = "",

        @ColumnInfo(name = "street_number")
        val estateStreetNumber: Int?,

        @ColumnInfo(name = "city")
        val estateCity: String,

        @ColumnInfo(name = "postal_code")
        val estateCityPostalCode: String?,

        @ColumnInfo(name = "picture_url")
        val pictureUrl: String?,

        @ColumnInfo(name = "availability")
        val estateAvailability: Boolean = true,

        @ColumnInfo(name = "employee")
        val estateEmployee: String,


        )

