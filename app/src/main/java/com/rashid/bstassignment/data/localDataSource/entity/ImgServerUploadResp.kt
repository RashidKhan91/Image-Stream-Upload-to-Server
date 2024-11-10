package com.rashid.bstassignment.data.localDataSource.entity

data class ImgServerUploadResp(
    val statusCode: String,
    val title: String?,
//  var is_branch: "",
    val message: String,
    val responseData: ResponseData
)
