package com.rashid.bstassignment.data.repository

import com.rashid.bstassignment.data.localDataSource.db.BSTDatabase
import com.rashid.bstassignment.data.localDataSource.entity.AssociatedDrug
import com.rashid.bstassignment.data.localDataSource.entity.DiseaseMedications
import com.rashid.bstassignment.domain.repository.MedicineLocalDbRepo
import javax.inject.Inject

class MedicineLocalRepoImp @Inject constructor(private val bstLocalDb : BSTDatabase) : MedicineLocalDbRepo{

    override suspend fun saveMedicationData(medicineData: DiseaseMedications) {
        bstLocalDb.diseaseMedicationDao().insertDiseaseMedications(medicineData)
    }

    override suspend fun getMedicationData(): DiseaseMedications {
        return bstLocalDb.diseaseMedicationDao().getDiseaseMedications()
    }

    override suspend fun deleteMedicineData() {
        bstLocalDb.diseaseMedicationDao().deleteAll()
    }


}