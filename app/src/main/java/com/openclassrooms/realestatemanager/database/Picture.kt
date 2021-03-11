package com.openclassrooms.realestatemanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(foreignKeys = [ForeignKey(
    entity = Estate::class,
    parentColumns = arrayOf("start_time_milli"),
    childColumns = arrayOf("start_time_milli"),
    onDelete = ForeignKey.CASCADE
)], tableName = "picture_table")

data class Picture(
    @PrimaryKey(autoGenerate = true)
    val pictureId: Long = 0L,

    @ColumnInfo
    val start_time_milli: Long = 1L,

    @ColumnInfo(name = "picture_url")
    val pictureUrl: String

)
