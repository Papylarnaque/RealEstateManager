package com.openclassrooms.realestatemanager.database.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GeometryAPIMap : Serializable {
    @SerializedName("location")
    val location: LocationAPIMap? = null
}
