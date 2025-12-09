package com.example.myapplication.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.Recommendation
import com.example.myapplication.ui.theme.AppPalette
import com.example.myapplication.viewmodel.NutritionAppViewModel

@Composable
fun DashboardScreen(
    viewModel: NutritionAppViewModel,
    onOpenDiary: () -> Unit,
    onOpenWater: () -> Unit,
    onOpenReports: () -> Unit,
    onOpenReference: () -> Unit
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    val profile = dashboardState.activeProfile

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPalette.BackgroundGradient)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Text(
                text = "Привет, ${profile?.fullName ?: "спортсмен"}",
                style = MaterialTheme.typography.headlineSmall,
                color = AppPalette.TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Ваша персональная программа питания и гидратации",
                color = AppPalette.TextSecondary
            )
        }

        item {
            MacroCard(
                calories = dashboardState.macroTargets.calories,
                protein = dashboardState.macroTargets.protein,
                fats = dashboardState.macroTargets.fats,
                carbs = dashboardState.macroTargets.carbs,
                consumedCalories = dashboardState.todayDiary.sumOf { it.calories }
            )
        }

        item {
            WaterSummaryCard(
                consumed = dashboardState.waterConsumed,
                goal = dashboardState.waterGoal,
                onAddQuickly = { viewModel.addWater(it) },
                onOpenDetails = onOpenWater
            )
        }

        item {
            QuickActions(onOpenDiary, onOpenReports, onOpenReference)
        }

        item {
            Text(
                text = "Персональные рекомендации",
                color = AppPalette.TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(dashboardState.recommendations) { recommendation ->
            RecommendationCard(recommendation = recommendation, onAdd = {
                val protein = recommendation.protein
                val carbs = recommendation.carbs
                val fats = recommendation.fats
                viewModel.addDiaryEntry(
                    mealType = com.example.myapplication.data.model.MealType.Snack,
                    productName = recommendation.title,
                    quantity = 100f,
                    unit = "г",
                    calories = recommendation.calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fats,
                    source = com.example.myapplication.data.model.DiaryEntrySource.Recommendation
                )
            })
        }
        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

@Composable
private fun MacroCard(
    calories: Int,
    protein: Int,
    fats: Int,
    carbs: Int,
    consumedCalories: Int
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, AppPalette.CardOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Целевой рацион",
                color = AppPalette.TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$consumedCalories / $calories ккал",
                color = AppPalette.AccentMint,
                style = MaterialTheme.typography.bodyLarge
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                MacroChip(label = "Белки", value = "$protein г")
                MacroChip(label = "Жиры", value = "$fats г")
                MacroChip(label = "Углеводы", value = "$carbs г")
            }
        }
    }
}

@Composable
private fun MacroChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = AppPalette.TextMuted, style = MaterialTheme.typography.bodySmall)
        Text(text = value, color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun WaterSummaryCard(
    consumed: Int,
    goal: Int,
    onAddQuickly: (Int) -> Unit,
    onOpenDetails: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, AppPalette.CardOutline)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Потребление воды", color = AppPalette.TextPrimary, style = MaterialTheme.typography.titleMedium)
            Text("$consumed / $goal мл", color = AppPalette.AccentLilac, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(250, 400, 500).forEach { ml ->
                    TextButton(onClick = { onAddQuickly(ml) }) {
                        Text("+${ml}мл", color = AppPalette.AccentLilac)
                    }
                }
            }
            Button(
                onClick = onOpenDetails,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppPalette.AccentMint)
            ) {
                Text("Детали и напоминания", color = Color.White)
            }
        }
    }
}

@Composable
private fun QuickActions(
    onOpenDiary: () -> Unit,
    onOpenReports: () -> Unit,
    onOpenReference: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionCard(title = "Дневник питания", description = "Добавьте приём пищи", onOpenDiary)
        ActionCard(title = "Графики", description = "Отслеживайте динамику", onOpenReports)
        ActionCard(title = "Справка", description = "Учебные материалы", onOpenReference)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.ActionCard(title: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F)),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(description, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: Recommendation,
    onAdd: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, AppPalette.CardOutline)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(recommendation.title, color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
            Text(recommendation.description, color = AppPalette.TextSecondary)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Б: ${recommendation.protein} г", color = AppPalette.AccentMint)
                Text("Ж: ${recommendation.fats} г", color = AppPalette.AccentPeach)
                Text("У: ${recommendation.carbs} г", color = AppPalette.AccentLilac)
                Text("${recommendation.calories} ккал", color = AppPalette.TextPrimary)
            }
            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppPalette.AccentMint)
            ) {
                Text("Добавить в дневник", color = Color.White)
            }
        }
    }
}

