package com.openclassrooms.realestatemanager.database.model

class EstateSearch(
    val type: String?,
    val priceRange: IntRange,
    val surfaceRange: IntRange,
    val createDateRange: LongRange,
    val soldStatus: Boolean,
    val soldDateRange: LongRange,
    val pictureMinNumber: Int
)