package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.navigation.MainNavigation
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.NutritionAppViewModel
import com.example.myapplication.viewmodel.NutritionAppViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val container = (application as MyApplicationApp).container
                val viewModelFactory = remember {
                    NutritionAppViewModelFactory(
                        container.stateRepository,
                        container.recommendationEngine,
                        container.smsService
                    )
                }
                val viewModel: NutritionAppViewModel = viewModel(factory = viewModelFactory)
                MainNavigation(viewModel)
            }
        }
    }
}