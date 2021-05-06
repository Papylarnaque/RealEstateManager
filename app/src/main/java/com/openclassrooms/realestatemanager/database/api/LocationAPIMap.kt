package com.openclassrooms.realestatemanager.database.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LocationAPIMap : Serializable {
    @SerializedName("lat")
    val lat: Double? = null

    @SerializedName("lng")
    val lng: Double? = null
}
