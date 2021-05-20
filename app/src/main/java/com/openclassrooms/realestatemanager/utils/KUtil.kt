package com.openclassrooms.realestatemanager.utils

import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


fun infoSnackBar(view: View, MESSAGE: String) {
    val snackBar = Snackbar.make(
        view, MESSAGE,
        Snackbar.LENGTH_LONG
    )
    val snackBarView = snackBar.view
    snackBarView.setBackgroundColor(Color.BLACK)
    val textView =
        snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    textView.setTextColor(Color.WHITE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER;
    } else {
        textView.gravity = Gravity.CENTER_HORIZONTAL;
    }
    snackBar.show()
}

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

fun formatPrice(price: Int): String {
    val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
    formatter.applyPattern("#,###,###,###")
    return formatter.format(price.toLong()).toString()
}


