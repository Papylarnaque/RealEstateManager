package com.openclassrooms.realestatemanager.utils

import android.content.Context
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.*

enum class Source {
    CAMERA, PICKER;

    override fun toString(): String {
        return name.toLowerCase(Locale.getDefault())
    }
}

fun getImagesFolder(context: Context): File {
    return File(context.filesDir, "images/").also {
        if (!it.exists()) {
            it.mkdir()
        }
    }
}

fun generateFilename(source: Source) = "$source-${System.currentTimeMillis()}.jpg"

fun copyImageFromStream(input: InputStream, outputStream: OutputStream) {
//    withContext(Dispatchers.IO) {
        input.copyTo(outputStream)
//    }




}