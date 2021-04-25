package com.openclassrooms.realestatemanager.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employee_table")
data class Employee(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "employee_id")
    val employeeId: Int,

    @ColumnInfo(name = "first_name")
    var employeeFirstName: String,

    @ColumnInfo(name = "last_name")
    var employeeLastName: String,

    var employeeFullName: String = "$employeeFirstName $employeeLastName",
)