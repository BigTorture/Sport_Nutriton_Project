package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.vet.AnimalDetailScreen
import com.example.myapplication.ui.vet.AnimalListScreen
import com.example.myapplication.ui.vet.AuthVetScreen
import com.example.myapplication.viewmodel.VetViewModel

@Composable
fun MainNavigation(viewModel: VetViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val start = if (uiState.isLoggedIn) "animals" else "auth"

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            navController.navigate("animals") {
                popUpTo("auth") { inclusive = true }
            }
        } else {
            navController.navigate("auth") {
                popUpTo(0)
            }
        }
    }

    NavHost(navController = navController, startDestination = start) {
        composable("auth") {
            AuthVetScreen(uiState = uiState, onLogin = { u, p -> viewModel.login(u, p) })
        }
        composable("animals") {
            AnimalListScreen(
                animals = uiState.animals,
                onAddAnimal = { viewModel.addAnimal(it) },
                onOpenAnimal = { id -> navController.navigate("animal/$id") }
            )
        }
        composable(
            route = "animal/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id")
            val animal = uiState.animals.find { it.id == id }
            AnimalDetailScreen(
                animal = animal,
                records = uiState.medicalRecords,
                vaccinations = uiState.vaccinations,
                operations = uiState.operations,
                reminders = uiState.reminders,
                onAddRecord = { title, desc, doctor ->
                    if (id != null) viewModel.addMedicalRecord(id, title, desc, doctor, null, null)
                },
                onAddVaccination = { vac, doctor, notes ->
                    if (id != null) viewModel.addVaccination(id, vac, doctor, notes)
                },
                onAddOperation = { type, doctor, notes ->
                    if (id != null) viewModel.addOperation(id, type, doctor, notes)
                },
                onAddReminder = { title, notes, type ->
                    if (id != null) viewModel.addReminder(id, title, notes, type)
                },
                onExportPdf = {
                    if (id != null) viewModel.exportAnimalPdf(id)
                }
            )
        }
    }
}
