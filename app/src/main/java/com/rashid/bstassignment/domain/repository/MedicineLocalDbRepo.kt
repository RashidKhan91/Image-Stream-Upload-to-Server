package com.rashid.bstassignment.domain.repository

import com.rashid.bstassignment.data.localDataSource.entity.AssociatedDrug
import com.rashid.bstassignment.data.localDataSource.entity.DiseaseMedications
import com.rashid.bstassignment.data.localDataSource.entity.UserImages

interface MedicineLocalDbRepo {

    suspend fun saveMedicationData(medicineData: DiseaseMedications)
    suspend fun getMedicationData() : DiseaseMedications
    suspend fun deleteMedicineData()
}