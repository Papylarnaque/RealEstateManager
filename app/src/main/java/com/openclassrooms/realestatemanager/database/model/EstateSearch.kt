package com.openclassrooms.realestatemanager.database.model

class EstateSearch(
    var type: String?,
    var priceRange: IntRange,
) {
//    fun toString(context: Context): String {
//        return StringBuilder().run {
//            append("\n")
//            append(context.getString(R.string.type))
//            append(": ")
//            append(type.toString())
//            toString().trim()
//        }
//    }
}