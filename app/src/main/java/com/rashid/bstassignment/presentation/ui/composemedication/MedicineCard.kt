package com.rashid.bstassignment.presentation.ui.composemedication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rashid.bstassignment.data.localDataSource.entity.AssociatedDrug
import com.rashid.bstassignment.data.localDataSource.entity.DiseaseMedications
import com.rashid.bstassignment.data.localDataSource.entity.Problem

@Composable
fun MedicineCard(associatedDrug: AssociatedDrug, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Name: ${associatedDrug.name}",
                style = MaterialTheme.typography.h6
            )
            Text("Dose: ${associatedDrug.dose}", style = MaterialTheme.typography.body1)
            Text("Strength: ${associatedDrug.strength}", style = MaterialTheme.typography.body1)
        }
    }
}
