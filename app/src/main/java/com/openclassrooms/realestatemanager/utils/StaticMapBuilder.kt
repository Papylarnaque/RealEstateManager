package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.database.model.DetailedEstate

const val URL_ZOOM = 14
const val URL_ZOOM_RATIO = 175
const val URL_MARKER_SIZE = "tiny"

class StaticMapBuilder {


    companion object {
        fun buildUrl(detailedEstate: DetailedEstate): String {

            val addressStr = AddressUtil.buildAddress(detailedEstate)

//            val number: Int? = detailedEstate.estate?.estateStreetNumber
//            val street: String? = detailedEstate.estate?.estateStreet
//            val city: String? = detailedEstate.estate?.estateCity
//            val postal: String? = detailedEstate.estate?.estateCityPostalCode
//
//            val addressBuilder: StringBuilder = StringBuilder()
//            addressBuilder.append(number)
//            addressBuilder.append("+")
//            addressBuilder.append(street)
//            addressBuilder.append("+")
//            addressBuilder.append(city)
//            addressBuilder.append("+")
//            addressBuilder.append(postal)

            val urlBase = "https://maps.googleapis.com/maps/api/staticmap?"
            val separator = "&"
            val urlCenter = "center="
            val urlZoom = "zoom=$URL_ZOOM"
            val urlSize = "size=$URL_ZOOM_RATIO"+"x$URL_ZOOM_RATIO"
            val urlMarkers = "markers="
            val urlMarkersSize = "size:$URL_MARKER_SIZE"
            val urlKey = "key="
            val key = BuildConfig.STATIC_MAP_KEY
            val strBuilder: StringBuilder = StringBuilder()

            strBuilder.append(urlBase)
            strBuilder.append(urlCenter)
            strBuilder.append(addressStr)
            strBuilder.append(separator)
            strBuilder.append(urlSize)
            strBuilder.append(separator)
            strBuilder.append(urlZoom)
            strBuilder.append(separator)
            strBuilder.append(urlMarkers)
            strBuilder.append(urlMarkersSize)
            strBuilder.append("%7C")
            strBuilder.append(addressStr)
            strBuilder.append(separator)
            strBuilder.append(urlKey)
            strBuilder.append(key)

            return strBuilder.trim().toString()

        }

    }

}