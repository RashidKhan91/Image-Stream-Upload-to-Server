package com.rashid.bstassignment.presentation.ui.composemedication

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.rashid.bstassignment.data.localDataSource.entity.AssociatedDrug

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginActivity { username ->
                navController.navigate("home/$username")
            }
        }

        composable("list") {
            RecyclerViewScreen()
        }

        composable(
            route = "home/{username}",
            arguments = listOf(navArgument("username") { defaultValue = "" })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            HomeScreen(navController, username)
        }

        composable(
            route = "details/{medicineJson}",
            arguments = listOf(navArgument("medicineJson") { defaultValue = "" })
        ) { backStackEntry ->
            val medicineJson = backStackEntry.arguments?.getString("medicineJson") ?: ""
            val medicine = Gson().fromJson(medicineJson, AssociatedDrug::class.java)
            MedicineDetailScreen(medicine)
        }
    }
}
