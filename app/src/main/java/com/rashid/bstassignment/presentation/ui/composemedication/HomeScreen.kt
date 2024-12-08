package com.rashid.bstassignment.presentation.ui.composemedication


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.rashid.bstassignment.presentation.viewmodels.MedicineViewModel

@Composable
fun HomeScreen(navController: NavHostController, username: String, viewModel: MedicineViewModel = hiltViewModel()) {
    val medicineState by viewModel.medicineState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMedicines()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("${getGreeting()}, $username!", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(16.dp))

        when (medicineState) {
            is MedicineViewModel.MedicationDataState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is MedicineViewModel.MedicationDataState.Success -> {
                val medicines = (medicineState as MedicineViewModel.MedicationDataState.Success).medicines
                if (medicines.problems.isNotEmpty()) {
                     // Get the first medicine object
                    LaunchedEffect(Unit) {
                        // delete old data and save the updated data
                        viewModel.deleteMedicineData()
                        viewModel.saveMedicineData(medicines)
                    }
                    MedicineCard(medicines.problems[0].Diabetes[0].medications[0].medicationsClasses[0].className[0].associatedDrug[0]) {
                        val medicineJson = Gson().toJson(medicines.problems[0].Diabetes[0].medications[0].medicationsClasses[0].className[0].associatedDrug[0])
                        Log.e("List<Diabete>",""+medicineJson)
                        navController.navigate("details/$medicineJson")
                    }
                } else {
                    // Handle the case where the list is empty
                    Text(
                        text = "No medicines available",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.h6
                    )
                }
            }
            is MedicineViewModel.MedicationDataState.Error -> {
                val errorMessage = (medicineState as MedicineViewModel.MedicationDataState.Error).errorMessage
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                LaunchedEffect(Unit) {
                    viewModel.getMedicineData()
                }

            }

            is MedicineViewModel.MedicationDataState.Exception -> {
                val exception = (medicineState as MedicineViewModel.MedicationDataState.Exception).exception
                Text(
                    text = "Exception: $exception",
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                LaunchedEffect(Unit) {
                    viewModel.getMedicineData()
                }
            }
            MedicineViewModel.MedicationDataState.Init -> {
            Text(
                text = "Welcome! Please select a medicine to view details.",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.h6
            )
        }

//            is MedicineViewModel.MedicationDataState.SuccessFromDb -> {
//                val medicines = (medicineState as MedicineViewModel.MedicationDataState.SuccessFromDb).medicines
//                if (medicines!=null) {
//
//                    MedicineCard(medicines) {
//                        val medicineJson = Gson().toJson(medicines)
//                        Log.e("List<Diabete>",""+medicineJson)
//                        navController.navigate("details/$medicineJson")
//                    }
//                } else {
//                    // Handle the case where the list is empty
//                    Text(
//                        text = "No medicines available",
//                        modifier = Modifier.align(Alignment.CenterHorizontally),
//                        style = MaterialTheme.typography.h6
//                    )
//                }
//            }
        }
    }
}

fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Good Morning"
        in 12..17 -> "Good Afternoon"
        else -> "Good Evening"
    }
}
