package com.openclassrooms.realestatemanager.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "picture_table",
    foreignKeys = [
        ForeignKey(
            entity = Estate::class,
            parentColumns = ["start_time_milli"],
            childColumns = ["estate_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Picture(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "picture_id")
    var pictureId: Long,

    @ColumnInfo(name = "picture_url")
    var url: String = "",

    @ColumnInfo(name = "estate_id")
    var estateId: Long? = null,

    @ColumnInfo(name = "picture_name")
    var displayName: String = "",

    @ColumnInfo(name = "order_number")
    var orderNumber: Int? = null
)