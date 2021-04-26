package com.openclassrooms.realestatemanager.utils

import java.io.InputStream
import java.io.OutputStream
import java.util.*

enum class Source {
    CAMERA, PICKER;

    override fun toString(): String {
        return name.toLowerCase(Locale.getDefault())
    }
}

fun generateFilename(source: Source) = "$source-${System.currentTimeMillis()}.jpg"

fun copyImageFromStream(input: InputStream, outputStream: OutputStream) {
//    withContext(Dispatchers.IO) {
        input.copyTo(outputStream)
//    }
}