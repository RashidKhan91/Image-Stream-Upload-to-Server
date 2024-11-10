package com.rashid.bstassignment.domain.repository

import com.rashid.bstassignment.data.baseResponse.BaseResponseResult
import com.rashid.bstassignment.data.localDataSource.entity.ImgServerUploadResp
import com.rashid.bstassignment.data.localDataSource.entity.UserImages
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import java.io.File

interface ImagePostRepo {

    suspend fun imageUploadToServer(token:String,userImages: MultipartBody.Part) : Flow<BaseResponseResult<ImgServerUploadResp, Any>>
    suspend fun uploadImageFileWM(imageFile: File,token:String):Flow<BaseResponseResult<ImgServerUploadResp, Any>>
    suspend fun saveImageToDb(imageObj: UserImages)
    suspend fun updateImageInDb(imageObj: UserImages)
    suspend fun getImageStatus(imageName: String) : String
    suspend fun getPendingImages() :  Flow<List<UserImages>>
}