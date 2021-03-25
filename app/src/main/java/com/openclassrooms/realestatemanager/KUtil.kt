package com.openclassrooms.realestatemanager

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


}