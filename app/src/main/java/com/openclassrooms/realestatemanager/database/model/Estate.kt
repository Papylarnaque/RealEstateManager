package com.openclassrooms.realestatemanager.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "estate_table",
        foreignKeys = [
        androidx.room.ForeignKey(
                entity = Employee::class,
                parentColumns = ["employee_id"],
                childColumns = ["employee_id"],
                onDelete = androidx.room.ForeignKey.NO_ACTION
        )])

data class Estate(

        @PrimaryKey
        @ColumnInfo(name = "start_time_milli")
        val startTime: Long = System.currentTimeMillis(),

        @ColumnInfo(name = "end_time_milli")
        val endTime: Long?,

        @ColumnInfo(name = "type_id")
        var estateTypeId: Int,

        @ColumnInfo(name = "price")
        val estatePrice: Int?,

        @ColumnInfo(name = "surface")
        val estateSurface: Int?,

        @ColumnInfo(name = "rooms_count")
        val estateRooms: Int?,

        @ColumnInfo(name = "description")
        var estateDescription: String = "",

        @ColumnInfo(name = "street")
        val estateStreet: String = "",

        @ColumnInfo(name = "street_number")
        val estateStreetNumber: Int?,

        @ColumnInfo(name = "city")
        val estateCity: String,
//
//        @ColumnInfo(name = "latlng")
//        val estateLatlng: LatLng,

        @ColumnInfo(name = "postal_code")
        val estateCityPostalCode: String?,

        @ColumnInfo(name = "employee_id")
        val employeeId: Int,

        @ColumnInfo(name = "poi_id")
        val estatePois: String,
        )

