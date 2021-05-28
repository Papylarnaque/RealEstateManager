package com.openclassrooms.realestatemanager.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "estate_with_poi_table",
    primaryKeys = ["estate_id", "poi_id"],
    foreignKeys = [
        ForeignKey(
            childColumns = ["estate_id"],
            entity = Estate::class,
            parentColumns = ["start_time_milli"]
        ),
        ForeignKey(
            childColumns = ["poi_id"],
            entity = Poi::class,
            parentColumns = ["poi_id"]
        )
    ],
    indices = [
        Index(value = ["estate_id", "poi_id"], unique = true),
        Index("estate_id"),
        Index("poi_id")
    ]
)
data class EstateWithPoi(
    @ColumnInfo(name = "estate_id")
    val estate_id: Long,

    @ColumnInfo(name = "poi_id")
    val poi_id: Int


)
