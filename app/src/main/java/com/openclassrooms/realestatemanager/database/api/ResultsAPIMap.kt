package com.openclassrooms.realestatemanager.database.api

import com.google.gson.annotations.SerializedName

class ResultsAPIMap {

    @SerializedName("status")
    val status: String? = null

    @SerializedName("results")
    val results: List<ResultAPIMap>? = null
}
