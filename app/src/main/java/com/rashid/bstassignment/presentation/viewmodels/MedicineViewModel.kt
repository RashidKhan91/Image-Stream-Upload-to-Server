package com.rashid.bstassignment.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rashid.bstassignment.data.baseResponse.BaseResponseResult
import com.rashid.bstassignment.data.localDataSource.entity.AssociatedDrug
import com.rashid.bstassignment.data.localDataSource.entity.DiseaseMedications
import com.rashid.bstassignment.data.localDataSource.entity.ImgServerUploadResp
import com.rashid.bstassignment.data.localDataSource.entity.UserImages
import com.rashid.bstassignment.domain.useCases.impl.MedicineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor(private val medicineUseCase: MedicineUseCase) :
    ViewModel() {

    private val _medicineState = MutableStateFlow<MedicationDataState>(MedicationDataState.Init)
    val medicineState: StateFlow<MedicationDataState> get() = _medicineState


    private fun isLoading() {
        _medicineState.value = MedicationDataState.Loading(true)
    }

    private fun exception(message: String) {
        _medicineState.value = MedicationDataState.Exception(message)
    }

    private fun hideLoading() {
        _medicineState.value = MedicationDataState.Loading(false)
    }

    fun loadMedicines() {
        viewModelScope.launch {
            isLoading()
            try {
                medicineUseCase.getMedicineList().onStart {
                    isLoading()
                }.catch { exception ->
                    hideLoading()
                    Log.e("medicine Exception=", exception.message.toString())
                    exception(exception.message.toString())
                }.collect { baseResult ->
                    when (baseResult) {
                        is BaseResponseResult.Error -> {
                            _medicineState.value = MedicationDataState.Error(baseResult.rawResponse)
                        }

                        is BaseResponseResult.Success -> {
                            _medicineState.value = MedicationDataState.Success(baseResult.data!!)
                        }

                        else -> {}
                    }
                    hideLoading()
                }
            } catch (e: Exception) {
                exception(e.message.toString())
            }
        }
    }

    fun saveMedicineData(medicineData: DiseaseMedications) {
        viewModelScope.launch {
            medicineUseCase.saveMedicineData(medicineData)
        }
    }

     fun getMedicineData() {
        viewModelScope.launch {
            try {
                val medicationData = medicineUseCase.getMedicineData()
                _medicineState.value = MedicationDataState.Success(medicationData)
            } catch (e: Exception) {
                Log.e(
                    "MedicineViewModel",
                    "Error fetching data from local DB: ${e.localizedMessage}"
                )
            }
        }
    }

    suspend fun deleteMedicineData() {
        return medicineUseCase.deleteMedicineData()
    }


    sealed class MedicationDataState {
        data object Init : MedicationDataState()
        data class Loading(val isloading: Boolean) : MedicationDataState()
        data class Success(val medicines: DiseaseMedications) : MedicationDataState()
        data class Error(val errorMessage: Any) : MedicationDataState()
        data class Exception(val exception: Any) : MedicationDataState()
    }
}
