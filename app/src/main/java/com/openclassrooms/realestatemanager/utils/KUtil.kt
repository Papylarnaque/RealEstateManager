package com.openclassrooms.realestatemanager.utils

import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.database.model.DetailedEstate
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

fun buildAddress(it: DetailedEstate): String {
    with(it.estate) {
        val number: Int? = this?.estateStreetNumber
        val street: String? = this?.estateStreet
        val city: String? = this?.estateCity
        val postal: String? = this?.estateCityPostalCode

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

fun formatPrice(price: Int): String {
    return NumberFormat.getCurrencyInstance(Locale.US).run {
        maximumFractionDigits = 0
        format(price.toFloat())
    }
}


