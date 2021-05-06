package com.openclassrooms.realestatemanager.service

import com.openclassrooms.realestatemanager.database.api.ResultsAPIMap
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIRequest {

    @GET("geocode/json")
    fun getGeocode(
        @Query("address") address: String?,
        @Query("key") key: String?
    ): Call<ResultsAPIMap?>?

}
