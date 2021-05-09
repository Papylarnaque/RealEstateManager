package com.openclassrooms.realestatemanager.database

import android.content.ClipData
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope


class EstateContentProvider : ContentProvider() {

    private lateinit var cContext: Context

    override fun onCreate(): Boolean {
        this.cContext = context!!
        return true
    }

    @Nullable
    override fun query(
        @NonNull uri: Uri,
        @Nullable projection: Array<String>?,
        @Nullable selection: String?,
        @Nullable selectionArgs: Array<String>?,
        @Nullable sortOrder: String?
    ): Cursor {
        val cursor: Cursor =
            EstateDatabase.getDatabase(cContext, CoroutineScope(GlobalScope.coroutineContext)).estateDao().getAllCursorEstates()

        cursor.setNotificationUri(cContext.contentResolver, uri)
        return cursor
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    @Nullable
    override fun getType(@NonNull uri: Uri): String {
        return "vnd.android.cursor.item/$AUTHORITY.$TABLE_NAME"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }

    companion object {
        // FOR DATA
        const val AUTHORITY = "com.openclassrooms.realestatemanager.provider"
        val TABLE_NAME: String = ClipData.Item::class.java.simpleName
        val URI_ITEM = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }
}