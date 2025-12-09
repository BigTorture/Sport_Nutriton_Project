package com.example.myapplication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import com.example.myapplication.utils.IconUtils
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.AppPalette
import com.example.myapplication.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    
    LaunchedEffect(Unit) {
        viewModel.loadSportsNutritionProducts()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPalette.BackgroundGradient)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Sports Nutrition",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppPalette.TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Browse refreshed picks tailored for performance.",
                color = AppPalette.TextSecondary,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.dp, AppPalette.CardOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            if (it.text.length >= 2) {
                                viewModel.searchProducts(it.text)
                            } else if (it.text.isEmpty()) {
                                viewModel.loadSportsNutritionProducts()
                            }
                        },
                        placeholder = {
                            Text(
                                text = "Search protein, BCAA, supplements...",
                                color = AppPalette.TextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = IconUtils.SafeSearch,
                                contentDescription = "Search",
                                tint = AppPalette.TextMuted
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppPalette.AccentMint,
                            unfocusedBorderColor = AppPalette.CardOutline,
                            focusedTextColor = AppPalette.TextPrimary,
                            unfocusedTextColor = AppPalette.TextPrimary,
                            cursorColor = AppPalette.AccentMint
                        )
                    )
                }
            }
            
            // Content
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = AppPalette.AccentMint
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading Sports Nutrition Products...",
                                color = AppPalette.TextPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error: ${error}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = AppPalette.AccentPeach
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadSportsNutritionProducts() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppPalette.AccentMint
                                ),
                                shape = RoundedCornerShape(30.dp)
                            ) {
                                Text("Retry", color = Color.White)
                            }
                        }
                    }
                }
                
                products.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No sports nutrition products found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = AppPalette.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadSportsNutritionProducts() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppPalette.AccentMint
                                ),
                                shape = RoundedCornerShape(30.dp)
                            ) {
                                Text("Refresh", color = Color.White)
                            }
                        }
                    }
                }
                
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 16.dp),
                        contentPadding = PaddingValues(bottom = 120.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(products.filter { product ->
                            try {
                                // Filter for sports nutrition products only
                                val title = product.title.lowercase()
                                val brand = product.brand.lowercase()
                                val category = product.category.lowercase()
                                
                                title.contains("protein") || 
                                title.contains("whey") || 
                                title.contains("bcaa") || 
                                title.contains("creatine") || 
                                title.contains("supplement") || 
                                title.contains("nutrition") ||
                                title.contains("fitness") ||
                                title.contains("muscle") ||
                                brand.contains("protein") ||
                                brand.contains("whey") ||
                                category.contains("supplements") ||
                                category.contains("nutrition")
                            } catch (e: Exception) {
                                false // Skip problematic products
                            }
                        }) { product ->
                            ProductCard(product = product)
                        }
                    }
                }
            }
        }
    }
}
