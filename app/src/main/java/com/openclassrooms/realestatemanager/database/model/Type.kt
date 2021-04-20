package com.openclassrooms.realestatemanager.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "type_table")
data class Type(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "type_id")
    val typeId: Long,

    @ColumnInfo(name = "name")
    var typeName: String,

)