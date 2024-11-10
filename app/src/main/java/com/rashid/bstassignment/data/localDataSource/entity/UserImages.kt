package com.rashid.bstassignment.data.localDataSource.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_images")
data class UserImages(

    @ColumnInfo var imagePath: String? = null,
    @ColumnInfo var imageName: String? = null,
    @ColumnInfo var imageDesc: String? = null,
    @ColumnInfo var isUploaded:Int = 0,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
