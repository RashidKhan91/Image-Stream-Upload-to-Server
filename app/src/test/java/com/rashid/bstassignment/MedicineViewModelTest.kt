package com.rashid.bstassignment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.rashid.bstassignment.data.baseResponse.BaseResponseResult
import com.rashid.bstassignment.data.localDataSource.entity.DiseaseMedications
import com.rashid.bstassignment.domain.useCases.impl.MedicineUseCase
import com.rashid.bstassignment.presentation.viewmodels.MedicineViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class MedicineViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var medicineUseCase: MedicineUseCase
    private lateinit var viewModel: MedicineViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)  // Use test dispatcher
        medicineUseCase = mock(MedicineUseCase::class.java)
        viewModel = MedicineViewModel(medicineUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMedicines emits success state when useCase returns success`() = runTest {
        // Mock success data
        val mockMedicines = DiseaseMedications(problems = listOf())
        val mockSuccessResponse = BaseResponseResult.Success(mockMedicines)

        // Mock the use case to return a successful response
        `when`(medicineUseCase.getMedicineList()).thenReturn(flow { emit(mockSuccessResponse) })

        // Observe the emitted states
        viewModel.medicineState.test {
            // Expect initial state as Init
            assertEquals(MedicineViewModel.MedicationDataState.Init, awaitItem())

            // Trigger the loadMedicines method
            viewModel.loadMedicines()

            assertEquals(MedicineViewModel.MedicationDataState.Loading(true), awaitItem())

            assertEquals(MedicineViewModel.MedicationDataState.Success(mockMedicines), awaitItem())
            assertEquals(MedicineViewModel.MedicationDataState.Loading(false), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }

        verify(medicineUseCase).getMedicineList()
    }

    @Test
    fun `loadMedicines emits error state when useCase returns error`() = runTest {
        // Mock error response
        val mockErrorResponse = BaseResponseResult.Error("Network Error")

        `when`(medicineUseCase.getMedicineList()).thenReturn(flow { emit(mockErrorResponse) })

        viewModel.medicineState.test {

            assertEquals(MedicineViewModel.MedicationDataState.Init, awaitItem())

            viewModel.loadMedicines()

            assertEquals(MedicineViewModel.MedicationDataState.Loading(true), awaitItem())

            assertEquals(MedicineViewModel.MedicationDataState.Error("Network Error"), awaitItem())

            assertEquals(MedicineViewModel.MedicationDataState.Loading(false), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }

        verify(medicineUseCase).getMedicineList()
    }

    @Test
    fun `loadMedicines emits exception state when useCase throws exception`() = runTest {
        // Mock exception
        val exceptionMessage = "Something went wrong!"
        `when`(medicineUseCase.getMedicineList()).thenAnswer { throw RuntimeException(exceptionMessage) }

        // Observe the emitted states
        viewModel.medicineState.test {

            assertEquals(MedicineViewModel.MedicationDataState.Init, awaitItem())

            viewModel.loadMedicines()

            assertEquals(MedicineViewModel.MedicationDataState.Loading(true), awaitItem())

            assertEquals(MedicineViewModel.MedicationDataState.Exception(exceptionMessage), awaitItem())

            assertEquals(MedicineViewModel.MedicationDataState.Loading(false), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }

        verify(medicineUseCase).getMedicineList()
    }
}
