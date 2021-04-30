package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.database.model.DetailedEstate

class StaticMapBuilder {


    companion object {
        fun buildUrl(detailedEstate: DetailedEstate): String {

            val number: Int? = detailedEstate.estate?.estateStreetNumber
            val street: String? = detailedEstate.estate?.estateStreet
            val city: String? = detailedEstate.estate?.estateCity

            val addressBuilder: StringBuilder = StringBuilder()
            addressBuilder.append(number)
            addressBuilder.append("+")
            addressBuilder.append(street)
            addressBuilder.append("+")
            addressBuilder.append(city)

            val urlBase = "https://maps.googleapis.com/maps/api/staticmap?"
            val separator = "&"
            val urlCenter = "center="
            val urlZoom = "zoom=14"
            val urlSize = "size=200x200"
            val urlMarkers = "markers="
            val urlMarkersSize = "size:tiny"
            val urlKey = "key="
            val key = BuildConfig.STATIC_MAP_KEY
            val strBuilder: StringBuilder = StringBuilder()

            strBuilder.append(urlBase)
            strBuilder.append(urlCenter)
            strBuilder.append(addressBuilder)
            strBuilder.append(separator)
            strBuilder.append(urlSize)
            strBuilder.append(separator)
            strBuilder.append(urlZoom)
            strBuilder.append(separator)
            strBuilder.append(urlMarkers)
            strBuilder.append(urlMarkersSize)
            strBuilder.append("%7C")
            strBuilder.append(addressBuilder)
            strBuilder.append(separator)
            strBuilder.append(urlKey)
            strBuilder.append(key)

            return strBuilder.trim().toString()

        }

    }

}