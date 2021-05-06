package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.database.model.DetailedEstate

class AddressUtil {

    companion object {

        fun buildAddress(detailedEstate: DetailedEstate): String {

            val number: Int? = detailedEstate.estate?.estateStreetNumber
            val street: String? = detailedEstate.estate?.estateStreet
            val city: String? = detailedEstate.estate?.estateCity
            val postal: String? = detailedEstate.estate?.estateCityPostalCode

            val addressBuilder: StringBuilder = StringBuilder()
            addressBuilder.append(number)
            addressBuilder.append("+")
            addressBuilder.append(street)
            addressBuilder.append("+")
            addressBuilder.append(city)
            addressBuilder.append("+")
            addressBuilder.append(postal)

            return addressBuilder.toString()
        }
    }
}