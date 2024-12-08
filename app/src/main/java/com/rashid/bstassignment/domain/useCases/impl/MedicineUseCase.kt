package com.rashid.bstassignment.domain.useCases.impl

import com.rashid.bstassignment.data.baseResponse.BaseResponseResult
import com.rashid.bstassignment.data.localDataSource.entity.AssociatedDrug
import com.rashid.bstassignment.data.localDataSource.entity.DiseaseMedications
import com.rashid.bstassignment.data.localDataSource.entity.ImgServerUploadResp
import com.rashid.bstassignment.data.localDataSource.entity.UserImages
import com.rashid.bstassignment.domain.repository.MedicineLocalDbRepo
import com.rashid.bstassignment.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class MedicineUseCase @Inject constructor(private val medicineRepo: MedicineRepository,
      private val medicineLocalDbRepo: MedicineLocalDbRepo) {

    suspend fun getMedicineList(): Flow<BaseResponseResult<DiseaseMedications, Any>> {
        return medicineRepo.getMedication()
    }

    suspend fun saveMedicineData(medicineData: DiseaseMedications) {
        medicineLocalDbRepo.saveMedicationData(medicineData)
    }


    suspend fun getMedicineData() : DiseaseMedications{
        return medicineLocalDbRepo.getMedicationData()
    }

    suspend fun deleteMedicineData() {
        return medicineLocalDbRepo.deleteMedicineData()
    }
}