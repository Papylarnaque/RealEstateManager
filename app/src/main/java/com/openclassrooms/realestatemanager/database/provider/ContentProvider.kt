package com.openclassrooms.realestatemanager.database.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.realestatemanager.database.EstateDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class ContentProvider : ContentProvider() {

    companion object {
        private const val AUTHORITY = "fr.openclassrooms.realestatemanager.database.provider"
        private const val TABLE_NAME = "estate_table"
        val uri: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        context?.let { context ->
            return EstateDatabase.getDatabase(context, CoroutineScope(SupervisorJob()))
                .estateDao()
                .getEstateList().apply {
                    setNotificationUri(context.contentResolver, uri)
                }
        }
        throw IllegalArgumentException("Failed to query estate list.")
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}