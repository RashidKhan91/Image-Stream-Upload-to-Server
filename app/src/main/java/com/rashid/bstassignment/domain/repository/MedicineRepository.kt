package com.rashid.bstassignment.domain.repository

import com.rashid.bstassignment.data.baseResponse.BaseResponseResult
import com.rashid.bstassignment.data.localDataSource.entity.DiseaseMedications
import com.rashid.bstassignment.data.localDataSource.entity.UserImages
import com.rashid.bstassignment.data.network.NetworkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface MedicineRepository {

    suspend fun getMedication() : Flow<BaseResponseResult<DiseaseMedications, Any>>
}
