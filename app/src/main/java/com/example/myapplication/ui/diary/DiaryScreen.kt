package com.example.myapplication.ui.diary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.local.LocalFoodDatabase
import com.example.myapplication.data.model.DiaryEntry
import com.example.myapplication.data.model.MealType
import com.example.myapplication.ui.theme.AppPalette
import com.example.myapplication.viewmodel.NutritionAppViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun DiaryScreen(viewModel: NutritionAppViewModel) {
    val appState by viewModel.appState.collectAsState()
    val profile = appState.profiles.find { it.id == appState.activeProfileId }
    val entries = appState.diaryEntries
        .filter { it.userId == profile?.id }
        .sortedByDescending { it.timestamp }
    val grouped = entries.groupBy { dateFormatter(it.timestamp) }
    val totals = entries.fold(Totals()) { acc, entry ->
        acc + Totals(entry.calories, entry.protein, entry.carbs, entry.fat)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPalette.BackgroundGradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Дневник питания",
            color = AppPalette.TextPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text("Записывайте продукты вручную или выбирайте из базы.", color = AppPalette.TextSecondary)

        SummaryCard(totals)
        ManualEntryCard(viewModel)
        QuickSelectCard(viewModel)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f, fill = false)) {
            grouped.forEach { (date, dayEntries) ->
                item {
                    Text(date, color = AppPalette.TextPrimary, style = MaterialTheme.typography.titleMedium)
                }
                items(dayEntries) { entry ->
                    DiaryEntryCard(entry = entry, onDelete = { viewModel.removeDiaryEntry(entry.id) })
                }
            }
        }
    }
}

private data class Totals(
    val calories: Int = 0,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fats: Float = 0f
) {
    operator fun plus(other: Totals) = Totals(
        calories + other.calories,
        protein + other.protein,
        carbs + other.carbs,
        fats + other.fats
    )
}

@Composable
private fun SummaryCard(totals: Totals) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, AppPalette.CardOutline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Сегодня", color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
            Text("${totals.calories} ккал", color = AppPalette.AccentMint, style = MaterialTheme.typography.bodyLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Б ${totals.protein.roundToInt()} г", color = AppPalette.TextSecondary)
                Text("Ж ${totals.fats.roundToInt()} г", color = AppPalette.TextSecondary)
                Text("У ${totals.carbs.roundToInt()} г", color = AppPalette.TextSecondary)
            }
        }
    }
}

@Composable
private fun ManualEntryCard(viewModel: NutritionAppViewModel) {
    var meal by remember { mutableStateOf(MealType.Breakfast) }
    var product by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("150") }
    var calories by remember { mutableStateOf("0") }
    var protein by remember { mutableStateOf("0") }
    var carbs by remember { mutableStateOf("0") }
    var fats by remember { mutableStateOf("0") }

    Card(
        colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, AppPalette.CardOutline)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Ручной ввод", color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
            MealTypeSelector(selected = meal, onSelected = { meal = it })
            OutlinedTextField(value = product, onValueChange = { product = it }, label = { Text("Продукт") }, modifier = Modifier.fillMaxWidth(), colors = diaryTextFieldColors())
            OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Количество, г/мл") }, colors = diaryTextFieldColors())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Ккал") }, modifier = Modifier.weight(1f), colors = diaryTextFieldColors())
                OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Б") }, modifier = Modifier.weight(1f), colors = diaryTextFieldColors())
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = fats, onValueChange = { fats = it }, label = { Text("Ж") }, modifier = Modifier.weight(1f), colors = diaryTextFieldColors())
                OutlinedTextField(value = carbs, onValueChange = { carbs = it }, label = { Text("У") }, modifier = Modifier.weight(1f), colors = diaryTextFieldColors())
            }
            Button(
                onClick = {
                    viewModel.addDiaryEntry(
                        mealType = meal,
                        productName = product,
                        quantity = quantity.toFloatOrNull() ?: 0f,
                        unit = "г",
                        calories = calories.toIntOrNull() ?: 0,
                        protein = protein.toFloatOrNull() ?: 0f,
                        carbs = carbs.toFloatOrNull() ?: 0f,
                        fat = fats.toFloatOrNull() ?: 0f
                    )
                    product = ""
                },
                enabled = product.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppPalette.AccentMint),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Добавить запись", color = Color.White)
            }
        }
    }
}

@Composable
private fun QuickSelectCard(viewModel: NutritionAppViewModel) {
    var selected by remember { mutableStateOf(LocalFoodDatabase.wholeFoods.first().name) }
    var grams by remember { mutableStateOf("150") }
    var meal by remember { mutableStateOf(MealType.Lunch) }

    Card(
        colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, AppPalette.CardOutline)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("База продуктов", color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
            MealTypeSelector(selected = meal, onSelected = { meal = it })
            DropdownSelector(
                title = "Выберите продукт",
                options = (LocalFoodDatabase.wholeFoods + LocalFoodDatabase.sportsNutrition).map { it.name },
                selected = selected,
                onSelected = { selected = it }
            )
            OutlinedTextField(value = grams, onValueChange = { grams = it }, label = { Text("Граммовка") }, colors = diaryTextFieldColors())
            Button(
                onClick = {
                    viewModel.quickAddFromFoodDatabase(selected, meal, grams.toIntOrNull() ?: 100)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppPalette.AccentLilac),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Добавить из базы", color = Color.White)
            }
        }
    }
}

@Composable
private fun MealTypeSelector(selected: MealType, onSelected: (MealType) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MealType.values().forEach { type ->
            val isSelected = selected == type
            TextButton(
                onClick = { onSelected(type) },
                shape = RoundedCornerShape(50),
            ) {
                Text(
                    text = type.name,
                    color = if (isSelected) AppPalette.AccentMint else AppPalette.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun DropdownSelector(
    title: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        TextButton(onClick = { expanded = true }) {
            Text("$title: $selected", color = AppPalette.TextPrimary)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = AppPalette.TextPrimary) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DiaryEntryCard(entry: DiaryEntry, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, AppPalette.CardOutline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${entry.mealType}: ${entry.productName}", color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
            Text(
                "${entry.quantity} ${entry.unit} — ${entry.calories} ккал (Б ${entry.protein} / Ж ${entry.fat} / У ${entry.carbs})",
                color = AppPalette.TextSecondary
            )
            Text("Источник: ${entry.source.name.lowercase()}", color = AppPalette.AccentMint, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(onClick = onDelete) {
                Text("Удалить", color = AppPalette.AccentPeach)
            }
        }
    }
}

private fun dateFormatter(timestamp: Long): String {
    val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(DateTimeFormatter.ofPattern("dd MMMM"))
}

@Composable
private fun diaryTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedTextColor = AppPalette.TextPrimary,
    unfocusedTextColor = AppPalette.TextPrimary,
    cursorColor = AppPalette.AccentMint,
    focusedIndicatorColor = AppPalette.AccentMint,
    unfocusedIndicatorColor = AppPalette.CardOutline,
    focusedLabelColor = AppPalette.TextSecondary,
    unfocusedLabelColor = AppPalette.TextSecondary,
    focusedPlaceholderColor = AppPalette.TextSecondary,
    unfocusedPlaceholderColor = AppPalette.TextSecondary
)

