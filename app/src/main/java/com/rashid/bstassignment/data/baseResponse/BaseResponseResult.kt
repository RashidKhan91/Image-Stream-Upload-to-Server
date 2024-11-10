package com.rashid.bstassignment.data.baseResponse

sealed class BaseResponseResult<out T : Any, out U : Any> {
    data class Success<T : Any>(val data: T?) : BaseResponseResult<T, Nothing>()
    data class Error<U : Any>(val rawResponse: U) : BaseResponseResult<Nothing, U>()
}