package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.navigation.MainNavigation
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.auth.AuthTokenStore
import com.example.myapplication.data.export.PdfExporter
import com.example.myapplication.data.repository.FakeVetRepository
import com.example.myapplication.viewmodel.VetViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<VetViewModel>(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return VetViewModel(
                                repository = FakeVetRepository(),
                                tokenStore = AuthTokenStore(applicationContext),
                                PdfExporter(applicationContext)

                            ) as T
                        }
                    }
                )
                MainNavigation(viewModel)
            }
        }
    }
}