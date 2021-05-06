package com.openclassrooms.realestatemanager.database.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResultAPIMap : Serializable {

//    @SerializedName("address_components")
//    var components: List<String?>? = null

    @SerializedName("formatted_address")
    var address: String? = null

    @SerializedName("geometry")
    var geometry: GeometryAPIMap? = null

    @SerializedName("place_id")
    var placeId: String? = null

    @SerializedName("types")
    var types: List<String>? = null
}

