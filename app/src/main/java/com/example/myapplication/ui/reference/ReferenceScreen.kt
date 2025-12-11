package com.example.myapplication.ui.reference

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.local.LocalFoodDatabase

@Composable
fun ReferenceScreen() {
    var query by remember { mutableStateOf("") }
    val articles = LocalFoodDatabase.referenceArticles
        .filter { (title, _) -> title.contains(query, ignoreCase = true) || query.isBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Справочный раздел", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Подробности о спортивном питании, методах применения и противопоказаниях.", color = Color.Gray)

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Поиск по темам") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(articles.entries.toList()) { (title, paragraphs) ->
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                        paragraphs.forEach {
                            Text("• $it", color = Color(0xFFBDBDBD))
                        }
                    }
                }
            }
        }
    }
}




