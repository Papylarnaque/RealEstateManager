package com.openclassrooms.realestatemanager.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.database.api.ResultAPIMap
import com.openclassrooms.realestatemanager.database.api.ResultsAPIMap
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import com.openclassrooms.realestatemanager.utils.AddressUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GeocodeService {

    private const val TAG = "GeocodeService"

    // Nearby Places API variables
//    private val listenGeocodeResults: MutableLiveData<HashMap<Long,ResultAPIMap>> =
//        MutableLiveData<HashMap<Long,ResultAPIMap>>()
//    val geocodeResults: LiveData<HashMap<Long,ResultAPIMap>> = listenGeocodeResults

    private val listenGeocodeResults: MutableLiveData<List<ResultAPIMap>> =
        MutableLiveData<List<ResultAPIMap>>()
    val geocodeResults: LiveData<List<ResultAPIMap>> = listenGeocodeResults

    fun getGeocode(detailedEstate: DetailedEstate) {
        val apiMap: APIRequest = APIClient.client.create(APIRequest::class.java)
        val nearbyPlaces: Call<ResultsAPIMap?>? = apiMap.getGeocode(
            AddressUtil.buildAddress(detailedEstate),
            BuildConfig.GEOCODE_API_KEY
        )
        nearbyPlaces!!.enqueue(object : Callback<ResultsAPIMap?> {
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
//                            listenGeocodeResults.value?.set(detailedEstate.estate!!.startTime, body.results[0])
                            listenGeocodeResults.value = body.results
                        }
//                        for (geocodeResult in body.results!!) {
//                            if (!placeDetailsResultHashmap.containsKey(nearbyPlacesResult.getPlaceId())) {
//                                getPlaceDetails(geocodeResult.geometry)
//                            }
//                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResultsAPIMap?>, t: Throwable) {
                Log.d(TAG, "getGeocode failure$t")
            }
        })
    }

}