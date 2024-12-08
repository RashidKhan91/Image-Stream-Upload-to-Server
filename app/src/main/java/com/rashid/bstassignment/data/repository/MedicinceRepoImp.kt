package com.rashid.bstassignment.data.repository

import com.rashid.bstassignment.data.baseResponse.BaseResponseResult
import com.rashid.bstassignment.data.localDataSource.entity.DiseaseMedications
import com.rashid.bstassignment.data.localDataSource.entity.ImgServerUploadResp
import com.rashid.bstassignment.data.localDataSource.entity.UserImages
import com.rashid.bstassignment.data.network.NetworkApi
import com.rashid.bstassignment.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class MedicinceRepoImp @Inject constructor(private val networkApi: NetworkApi) : MedicineRepository{

    override suspend fun getMedication(): Flow<BaseResponseResult<DiseaseMedications, Any>> {
        return flow {
            val imagePostResponse = networkApi.getMedicines()
            if (imagePostResponse.isSuccessful) {
                emit(BaseResponseResult.Success(imagePostResponse.body()))
            } else {
                emit(BaseResponseResult.Error(imagePostResponse.message()))
            }
        }
    }

}