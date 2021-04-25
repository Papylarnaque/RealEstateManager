package com.openclassrooms.realestatemanager.utils

import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar


const val STRING_SEPARATOR = "||"

class KUtil {

    companion object {
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
    }


}