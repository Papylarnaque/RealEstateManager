package com.openclassrooms.realestatemanager.database.model

import androidx.room.Embedded
import androidx.room.Relation


class EstateAllPictures {
    @Embedded
    var estate: Estate? = null

    @Relation(parentColumn = "start_time_milli", entityColumn = "estate_id", entity = Picture::class)
    var pictures: List<Picture>? = null
}