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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.AppPalette
import com.example.myapplication.utils.IconUtils

@Composable
fun CartScreen() {
    // Mock cart data
    val cartItems = remember { mutableStateListOf(
        CartItem("Whey Protein 100%", "Optimum Nutrition", 49.99, 2),
        CartItem("BCAA Complex", "Dymatize", 29.99, 1),
        CartItem("Creatine Monohydrate", "MuscleTech", 19.99, 3)
    ) }
    
    val total = cartItems.sumOf { it.price * it.quantity }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPalette.BackgroundGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Shopping Cart",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppPalette.TextPrimary
                )
            }
            
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🛒",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your cart is empty",
                            fontSize = 18.sp,
                            color = AppPalette.TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add some products to get started",
                            fontSize = 14.sp,
                            color = AppPalette.TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemCard(
                            item = item,
                            onRemove = { cartItems.remove(item) },
                            onQuantityChange = { newQuantity ->
                                val index = cartItems.indexOf(item)
                                if (index != -1) {
                                    cartItems[index] = item.copy(quantity = newQuantity)
                                }
                            }
                        )
                    }
                }
                
                // Checkout Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppPalette.CardSurface)
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total:",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppPalette.TextPrimary
                            )
                            Text(
                                text = "$${String.format("%.2f", total)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppPalette.AccentMint
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { /* Checkout logic */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppPalette.AccentMint
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Checkout",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppPalette.CardSurface
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AppPalette.CardOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image Placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(AppPalette.CardAltSurface, RoundedCornerShape(12.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💪", fontSize = 24.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Product Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppPalette.TextPrimary
                )
                Text(
                    text = item.brand,
                    fontSize = 14.sp,
                    color = AppPalette.TextSecondary
                )
                Text(
                    text = "$${String.format("%.2f", item.price)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppPalette.AccentMint
                )
            }
            
            // Quantity Controls
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { 
                        if (item.quantity > 1) {
                            onQuantityChange(item.quantity - 1)
                        }
                    }
                ) {
                    Text(
                        text = "-",
                        color = AppPalette.TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = item.quantity.toString(),
                    color = AppPalette.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                IconButton(
                    onClick = { onQuantityChange(item.quantity + 1) }
                ) {
                    Text(
                        text = "+",
                        color = AppPalette.TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Remove Button
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = IconUtils.SafeDelete,
                    contentDescription = "Remove",
                    tint = AppPalette.AccentPeach
                )
            }
        }
    }
}

data class CartItem(
    val name: String,
    val brand: String,
    val price: Double,
    val quantity: Int
)
