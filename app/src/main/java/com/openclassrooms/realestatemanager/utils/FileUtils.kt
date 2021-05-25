package com.openclassrooms.realestatemanager.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.*

enum class Source {
    CAMERA, PICKER;

    override fun toString(): String {
        return name.lowercase(Locale.getDefault())
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
        input.copyTo(outputStream)
}

class MimeTypesUtil : ActivityResultContract<Array<String>, Uri?>() {
    override fun createIntent(
        context: Context,
        input: Array<String>
    ): Intent {
        return Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("*/*")
            .putExtra(Intent.EXTRA_MIME_TYPES, input);
    }

    override fun getSynchronousResult(
        context: Context,
        input: Array<String>
    ): SynchronousResult<Uri?>? {
        return null
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }
}