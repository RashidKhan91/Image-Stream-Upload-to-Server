package com.rashid.bstassignment.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
//import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
//import androidx.work.workDataOf
import com.rashid.bstassignment.data.baseResponse.BaseResponseResult
import com.rashid.bstassignment.data.localDataSource.entity.ImgServerUploadResp
import com.rashid.bstassignment.data.localDataSource.entity.UserImages
import com.rashid.bstassignment.data.worker.ImageUploadWorker
import com.rashid.bstassignment.domain.useCases.impl.ImagePostUseCase
import com.rashid.bstassignment.domain.useCases.impl.ObserveNetworkStatusUseCaseImp
import com.rashid.bstassignment.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ImageUpdateVM @Inject constructor(private var imageUseCase: ImagePostUseCase,
                                        private val observeNetworkStatusUseCaseImp: ObserveNetworkStatusUseCaseImp,
                                        private val workManager: WorkManager
    ) : ViewModel() {

    val isNetworkConnected: StateFlow<Boolean> = observeNetworkStatusUseCaseImp()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _imageState = MutableStateFlow<ImageUploadingState>(ImageUploadingState.Init)
    val imageState : StateFlow<ImageUploadingState> get() = _imageState

    private fun isLoading(){
        _imageState.value=ImageUploadingState.Loading(true)
    }

    private fun exception(message: String){
        _imageState.value= ImageUploadingState.Exception(message)
    }

    private fun hideLoading(){
        _imageState.value=ImageUploadingState.Loading(false)
    }

    fun imagePostToServer(filePath:String, imageObject: MultipartBody.Part){
        viewModelScope.launch {
            try {
                val authorizationToken = "Bearer ${Constants.BEARER_TOKEN}"
            imageUseCase.imagePostToServer(authorizationToken,imageObject).onStart {
                isLoading()
            }.catch {exception ->
                hideLoading()
                Log.e("Image Exception=",exception.message.toString())
                exception(exception.message.toString())
            }.collect { baseResult ->
                when (baseResult) {
                    is BaseResponseResult.Error -> {
                        _imageState.value = ImageUploadingState.Error(baseResult.rawResponse)
                        enqueueUploadWork(filePath)
                    }

                    is BaseResponseResult.Success -> {
                        _imageState.value = ImageUploadingState.Success(baseResult.data!!)
                    }

                    else -> {}
                }
            }
        } catch (e: Exception) {
                exception(e.message.toString())
            }
        }
    }

    fun saveImageToDb (userImage:UserImages){
        viewModelScope.launch {
            imageUseCase.saveImageToDb(userImage)
        }
    }
    fun updateImageToDb (userImage:UserImages){
        viewModelScope.launch {
            imageUseCase.updateImageToDb(userImage)
        }
    }

    suspend fun getImageStatus (imageName:String):String {
          return  imageUseCase.getImageStatus(imageName)
    }

    private fun enqueueUploadWork(filePath: String) {
        val uploadWorkRequest = OneTimeWorkRequestBuilder<ImageUploadWorker>()
            .setInputData(workDataOf("file_path" to filePath))
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()

        workManager.enqueue(uploadWorkRequest)
    }

    sealed class ImageUploadingState {
        data object Init : ImageUploadingState()
        data class Loading (val isloading: Boolean) : ImageUploadingState()
        data class Success (val imagePostResponse : ImgServerUploadResp) : ImageUploadingState()
        data class Error (val rawResponse: Any) : ImageUploadingState()
        data class Exception (val exception: Any) : ImageUploadingState()
    }
}