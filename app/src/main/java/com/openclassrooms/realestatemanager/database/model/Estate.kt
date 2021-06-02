package com.openclassrooms.realestatemanager.database.model

import android.content.ContentValues
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

        @ColumnInfo(name = "postal_code")
        val estateCityPostalCode: String?,

        @ColumnInfo(name = "employee_id", index = true)
        val employeeId: Int,
        )

{
        companion object {
                fun fromContentValues(it: ContentValues) : Estate
                { // For Content Provider Insert/Update purposes
                        return Estate(it.getAsLong("startTime"),
                        it.getAsLong("endTime"),
                        it.getAsInteger("estateTypeId"),
                        it.getAsInteger("price"),
                        it.getAsInteger("surface"),
                        it.getAsInteger("rooms_count"),
                        it.getAsString("description"),
                        it.getAsString("street"),
                        it.getAsInteger("street_number"),
                        it.getAsString("city"),
                        it.getAsString("postal_code"),
                        it.getAsInteger("employee_id")
                        )
                }
        }
}