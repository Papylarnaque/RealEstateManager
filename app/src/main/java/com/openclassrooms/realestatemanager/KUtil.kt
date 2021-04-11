package com.openclassrooms.realestatemanager

import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar


const val STRING_SEPARATOR = "||"

class KUtil {

    /**
     * Convert an Array<String> to a trimmed String
     * @param STRING_SEPARATOR
     */
    fun ArrayToString(array: Array<String>): String {
        val sb = StringBuffer()
        for (a in array) {
            // add element to string
            sb.append(a)
            // add separator except for last element
            if (a != array[array.size - 1]) sb.append(STRING_SEPARATOR)
        }
        return sb.toString()
    }

    /**
     * Convert a String to an Array<String>
     * @param STRING_SEPARATOR
     */
    fun StringToArray(string: String): Array<String> {
        return string.split(STRING_SEPARATOR).toTypedArray()
    }

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