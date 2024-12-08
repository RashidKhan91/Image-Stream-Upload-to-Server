package com.rashid.bstassignment.data.localDataSource.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rashid.bstassignment.data.localDataSource.dao.DiseaseMedicationDao
import com.rashid.bstassignment.data.localDataSource.dao.UserImageDao
import com.rashid.bstassignment.data.localDataSource.entity.AssociatedDrug
import com.rashid.bstassignment.data.localDataSource.entity.Converters
import com.rashid.bstassignment.data.localDataSource.entity.DiseaseMedications
import com.rashid.bstassignment.data.localDataSource.entity.UserImages
import com.rashid.bstassignment.utils.Constants


@Database(entities = [UserImages::class, DiseaseMedications::class], version = 1)
@TypeConverters(Converters::class)
abstract class BSTDatabase : RoomDatabase() {

    abstract fun userImageDao(): UserImageDao
    abstract fun diseaseMedicationDao(): DiseaseMedicationDao

    companion object {
        @Volatile
        private var INSTANCE: BSTDatabase? = null

        fun getDatabase(context: Context): BSTDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BSTDatabase::class.java,
                    Constants.BST_DATABASE
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}