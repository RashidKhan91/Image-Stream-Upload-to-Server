package com.rashid.bstassignment.data.localDataSource.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rashid.bstassignment.data.localDataSource.dao.UserImageDao
import com.rashid.bstassignment.data.localDataSource.entity.UserImages
import com.rashid.bstassignment.utils.Constants


@Database(entities = [UserImages::class], version = 1)
abstract class BSTDatabase : RoomDatabase() {

    abstract fun userImageDao(): UserImageDao

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