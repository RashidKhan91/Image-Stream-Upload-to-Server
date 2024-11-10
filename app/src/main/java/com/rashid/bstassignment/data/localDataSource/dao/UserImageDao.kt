package com.rashid.bstassignment.data.localDataSource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rashid.bstassignment.data.localDataSource.entity.UserImages
import kotlinx.coroutines.flow.Flow

@Dao
interface UserImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: UserImages)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImageList(image: List<UserImages>)

    @Query("SELECT * FROM user_images WHERE isUploaded = 0")
     fun getPendingImages(): Flow<List<UserImages>>

    @Query("""
        SELECT 
            CASE 
                WHEN isUploaded = 0 THEN 'Pending'
                ELSE 'True'
            END AS status
        FROM user_images
        WHERE imageName = :imageName
    """)
    suspend fun getImageStatus(imageName: String): String

    @Update
    suspend fun updateUserImage(updateUserImage: UserImages)

    @Query("DELETE FROM user_images WHERE isUploaded = 1")
    suspend fun deleteItem()

    @Query("DELETE FROM user_images")
    suspend fun deleteAllImages()

}