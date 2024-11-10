package com.rashid.bstassignment.data.network

import com.rashid.bstassignment.data.localDataSource.entity.ImgServerUploadResp
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface NetworkApi {


    @Multipart
    @POST("provider/profile")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part picture: MultipartBody.Part
    ): Response<ImgServerUploadResp>

}