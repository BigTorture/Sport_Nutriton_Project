package com.example.myapplication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.AppPalette
import com.example.myapplication.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProteinListScreen(
    viewModel: ProductViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var selectedFilter by remember { mutableStateOf("All Proteins") }
    
    val proteinFilters = listOf("All Proteins", "Whey Protein", "Casein", "Plant Protein", "Mass Gainers")
    
    LaunchedEffect(Unit) {
        viewModel.loadProteinProducts()
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
                text = "Protein Boutique",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppPalette.TextPrimary
            )
            Text(
                text = "Dial-in blends by goal—whey, plant, gainer & more.",
                color = AppPalette.TextSecondary,
                fontSize = 15.sp
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface),
                border = BorderStroke(1.dp, AppPalette.CardOutline),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Premium protein supplements for muscle building",
                        fontSize = 15.sp,
                        color = AppPalette.TextSecondary
                    )
                }
            }
            
            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                proteinFilters.forEach { filter ->
                    FilterChip(
                        onClick = { 
                            selectedFilter = filter
                            viewModel.filterProteins(filter)
                        },
                        label = { 
                            Text(
                                text = filter,
                                color = if (selectedFilter == filter) AppPalette.TextPrimary else AppPalette.TextSecondary
                            )
                        },
                        selected = selectedFilter == filter,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppPalette.ChipSelected,
                            containerColor = AppPalette.ChipDefault,
                            selectedLabelColor = AppPalette.TextPrimary
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
                                text = "Loading Protein Products...",
                                color = Color.White,
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
                                onClick = { viewModel.loadProteinProducts() },
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
                                text = "No protein products found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = AppPalette.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadProteinProducts() },
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
                                val title = product.title.lowercase()
                                val brand = product.brand.lowercase()
                                val category = product.category.lowercase()
                                
                                // Filter for protein products specifically
                                val isProtein = title.contains("protein") || 
                                              title.contains("whey") || 
                                              brand.contains("protein") ||
                                              brand.contains("whey") ||
                                              category.contains("protein")
                                
                                when (selectedFilter) {
                                    "All Proteins" -> isProtein
                                    "Whey Protein" -> title.contains("whey") || brand.contains("whey")
                                    "Casein" -> title.contains("casein") || brand.contains("casein")
                                    "Plant Protein" -> title.contains("plant") || title.contains("vegan") || title.contains("soy")
                                    "Mass Gainers" -> title.contains("mass") || title.contains("gainer") || title.contains("weight")
                                    else -> isProtein
                                }
                            } catch (e: Exception) {
                                false
                            }
                        }) { product ->
                            ProteinCard(product = product)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProteinCard(
    product: com.example.myapplication.data.NutritionProduct,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Product Image Container with White Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.White)
                    .padding(12.dp)
            ) {
                if (!product.image.isNullOrEmpty()) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = product.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Protein-themed fallback
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💪",
                            fontSize = 48.sp
                        )
                    }
                }
                
                // Protein Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            Color(0xFF4CAF50),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "PROTEIN",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Product Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Product Title
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Brand
                Text(
                    text = product.brand,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4CAF50),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Protein Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⭐ ${String.format("%.1f", product.rating)}",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${product.stock} left",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Price
                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Add to Cart Button
                Button(
                    onClick = { /* Add to cart logic */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Add to Cart",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
