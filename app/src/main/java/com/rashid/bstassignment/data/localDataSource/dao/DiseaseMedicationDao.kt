package com.rashid.bstassignment.data.localDataSource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rashid.bstassignment.data.localDataSource.entity.DiseaseMedications


@Dao
interface DiseaseMedicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiseaseMedications(diseaseMedications: DiseaseMedications)

    @Query("SELECT * FROM disease_medications")
    suspend fun getDiseaseMedications(): DiseaseMedications

    @Query("DELETE FROM disease_medications")
    suspend fun deleteAll()
}
