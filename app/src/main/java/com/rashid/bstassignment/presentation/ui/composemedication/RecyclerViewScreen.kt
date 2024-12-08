package com.rashid.bstassignment.presentation.ui.composemedication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecyclerViewScreen() {
    val items = (1..20).map { "Item $it" } // Sample data

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Item List", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items) { item ->
                ListItem(item)
            }
        }
    }
}

@Composable
fun ListItem(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.body1
        )
    }
}
