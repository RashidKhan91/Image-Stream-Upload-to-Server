package com.rashid.bstassignment.domain.useCases.impl

import com.rashid.bstassignment.data.baseResponse.BaseResponseResult
import com.rashid.bstassignment.data.localDataSource.entity.ImgServerUploadResp
import com.rashid.bstassignment.data.localDataSource.entity.UserImages
import com.rashid.bstassignment.domain.repository.ImagePostRepo
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class ImagePostUseCase @Inject constructor(private val imageRepo: ImagePostRepo){

     suspend fun imagePostToServer(token:String, imagePostRequest: MultipartBody.Part): Flow<BaseResponseResult<ImgServerUploadResp, Any>> {
        return imageRepo.imageUploadToServer(token,imagePostRequest)
    }

     suspend fun saveImageToDb(userImage: UserImages) {
        imageRepo.saveImageToDb(userImage)
    }

     suspend fun updateImageToDb(userImage: UserImages) {
        imageRepo.updateImageInDb(userImage)
    }

     suspend fun getImageStatus(imageName: String) : String{
        return imageRepo.getImageStatus(imageName)
    }
}