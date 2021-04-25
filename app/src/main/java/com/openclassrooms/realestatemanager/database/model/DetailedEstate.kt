package com.openclassrooms.realestatemanager.database.model

import androidx.room.Embedded
import androidx.room.Relation


class DetailedEstate {
    @Embedded
    var estate: Estate? = null

    @Relation(parentColumn = "start_time_milli", entityColumn = "estate_id", entity = Picture::class)
    var pictures: List<Picture>? = null

    @Relation(parentColumn = "type_id", entityColumn = "type_id", entity = Type::class)
    var type: Type? = null

    @Relation(parentColumn = "employee_id", entityColumn = "employee_id", entity = Employee::class)
    var employee: Employee? = null

    @Relation(parentColumn = "poi_id", entityColumn = "poi_id", entity = Poi::class)
    var pois: Poi? = null
}