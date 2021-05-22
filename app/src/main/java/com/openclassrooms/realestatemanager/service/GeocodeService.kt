package com.openclassrooms.realestatemanager.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.database.api.EstateGeocode
import com.openclassrooms.realestatemanager.database.api.ResultAPIMap
import com.openclassrooms.realestatemanager.database.api.ResultsAPIMap
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.utils.buildAddress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GeocodeService {

    private const val TAG = "GeocodeService"

    // Nearby Places API variables
    private val listenEstateGeocode: MutableLiveData<EstateGeocode> =
        MutableLiveData<EstateGeocode>()
    val estateGeocode: LiveData<EstateGeocode> = listenEstateGeocode

    fun getGeocode(detailedEstate: DetailedEstate) {
        val apiMap: APIRequest = APIClient.client.create(APIRequest::class.java)
        val geocodes: Call<ResultsAPIMap?>? = apiMap.getGeocode(
            buildAddress(detailedEstate),
            BuildConfig.GEOCODE_API_KEY
        )
        geocodes!!.enqueue(object : Callback<ResultsAPIMap?> {
            override fun onResponse(
                call: Call<ResultsAPIMap?>,
                response: Response<ResultsAPIMap?>
            ) {
                if (response.isSuccessful) {
                    val body: ResultsAPIMap? = response.body()
                    if (body != null) {
                        Log.d(TAG, "getGeocode successful $body")
                        if (body.results != null) {
                            // keep first result as the right one
                            convertToEstateGeocode(detailedEstate, body.results)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResultsAPIMap?>, t: Throwable) {
                Log.d(TAG, "getGeocode failure$t")
            }
        })
    }

    private fun convertToEstateGeocode(value: DetailedEstate, value1: List<ResultAPIMap>) {
        listenEstateGeocode.value = EstateGeocode(
            startTime = value.estate!!.startTime,
            endTime = value.estate!!.endTime,
            lat = value1[0].geometry?.location?.lat,
            lng = value1[0].geometry?.location?.lng,
            address = value1[0].address
        )
    }


}