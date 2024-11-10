package com.rashid.bstassignment.data.repository

import com.rashid.bstassignment.data.network.NetworkApi
import com.rashid.bstassignment.data.baseResponse.BaseResponseResult
import com.rashid.bstassignment.data.localDataSource.db.BSTDatabase
import com.rashid.bstassignment.data.localDataSource.entity.ImgServerUploadResp
import com.rashid.bstassignment.data.localDataSource.entity.UserImages
import com.rashid.bstassignment.domain.repository.ImagePostRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ImagePostRepoImpl @Inject constructor(private val networkApi: NetworkApi,
                                            private val bstLocalDb:BSTDatabase) :
    ImagePostRepo {

    override suspend fun imageUploadToServer(token:String,userImage: MultipartBody.Part):
            Flow<BaseResponseResult<ImgServerUploadResp, Any>> {
        return flow {
            val imagePostResponse = networkApi.uploadImage(token,userImage)
            if (imagePostResponse.isSuccessful) {
                emit(BaseResponseResult.Success(imagePostResponse.body()))
            } else {
                emit(BaseResponseResult.Error(imagePostResponse.message()))
            }
        }
    }

    override suspend fun uploadImageFileWM(
        imageFile: File,
        token: String
    ): Flow<BaseResponseResult<ImgServerUploadResp, Any>> {
        var userImages = UserImages()
        userImages.isUploaded=0
        userImages.imagePath = imageFile.path.toString()
        userImages.imageName = imageFile.name
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("picture", imageFile.name, requestFile)
        return flow {
            val imagePostResponse = networkApi.uploadImage(token,body)
            if (imagePostResponse.isSuccessful) {
                emit(BaseResponseResult.Success(imagePostResponse.body()))
            } else {
                emit(BaseResponseResult.Error(imagePostResponse.message()))
            }
        }
    }

    override suspend fun saveImageToDb(imageObj: UserImages) {
        bstLocalDb.userImageDao().insertImage(imageObj)
    }

    override suspend fun updateImageInDb(imageObj: UserImages) {
        bstLocalDb.userImageDao().updateUserImage(imageObj)
    }
    override suspend fun getImageStatus(imageName: String) : String {
       return  bstLocalDb.userImageDao().getImageStatus(imageName)
    }

    override suspend fun getPendingImages(): Flow<List<UserImages>> {
        return  bstLocalDb.userImageDao().getPendingImages()
    }
}