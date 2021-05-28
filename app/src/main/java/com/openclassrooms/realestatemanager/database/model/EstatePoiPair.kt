package com.openclassrooms.realestatemanager.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(
    tableName = "estate_poi_pair_table",
    primaryKeys = ["estate_id"]
)
data class EstatePoiPair(

    @Embedded
    var estate: Estate,
    @Relation(
        parentColumn = "start_time_milli",
        entity = Poi::class,
        entityColumn = "poi_id",
        associateBy = Junction(
            value = EstateWithPoi::class,
            parentColumn = "estate_id",
            entityColumn = "poi_id"
        )
    )
    var poiList: List<Poi>
)
