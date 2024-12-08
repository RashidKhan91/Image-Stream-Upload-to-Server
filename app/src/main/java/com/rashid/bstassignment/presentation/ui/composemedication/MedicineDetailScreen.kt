package com.rashid.bstassignment.presentation.ui.composemedication


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rashid.bstassignment.data.localDataSource.entity.AssociatedDrug
import com.rashid.bstassignment.data.localDataSource.entity.Diabete

@Composable
fun MedicineDetailScreen(associatedDrug: AssociatedDrug) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Name: ${associatedDrug.name}", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Dose: ${associatedDrug.dose}", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Strength: ${associatedDrug.strength}", style = MaterialTheme.typography.h5)
    }
}
