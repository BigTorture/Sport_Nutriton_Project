package com.example.myapplication.ui.water

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.example.myapplication.ui.theme.AppPalette
import com.example.myapplication.viewmodel.NutritionAppViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WaterTrackerScreen(viewModel: NutritionAppViewModel) {
    val dashboard by viewModel.dashboardState.collectAsState()
    val appState by viewModel.appState.collectAsState()
    val profile = dashboard.activeProfile
    val logs = appState.waterLogs.filter { it.userId == profile?.id }.sortedByDescending { it.timestamp }
    var customAmount by remember { mutableStateOf("300") }
    var waterGoal by remember { mutableStateOf(profile?.waterGoalMl?.toString() ?: "2500") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPalette.BackgroundGradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Потребление воды", color = AppPalette.TextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Напоминания и журнал гидратации.", color = AppPalette.TextSecondary)

        ProgressCard(consumed = dashboard.waterConsumed, goal = dashboard.waterGoal)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            listOf(200, 350, 500, 700).forEach { amount ->
                Button(
                    onClick = { viewModel.addWater(amount) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AppPalette.AccentMint),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("+${amount}мл", color = Color.White)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = customAmount,
                onValueChange = { customAmount = it },
                label = { Text("Свое значение, мл") },
                modifier = Modifier.weight(1f),
                colors = waterFieldColors()
            )
            Button(
                onClick = { viewModel.addWater(customAmount.toIntOrNull() ?: 0) },
                colors = ButtonDefaults.buttonColors(containerColor = AppPalette.AccentLilac),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Добавить", color = Color.White)
            }
        }

        OutlinedTextField(
            value = waterGoal,
            onValueChange = {
                waterGoal = it
                val value = it.toIntOrNull()
                if (value != null) viewModel.updateWaterGoal(value)
            },
            label = { Text("Суточная цель, мл") },
            modifier = Modifier.fillMaxWidth(),
            colors = waterFieldColors()
        )

        Divider(color = AppPalette.CardOutline)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f, fill = false)) {
            items(logs) { log ->
                Card(colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, AppPalette.CardOutline)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${log.amountMl} мл", color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
                        Text(formatLogTime(log.timestamp), color = AppPalette.TextSecondary)
                        TextButton(onClick = { viewModel.deleteWaterLog(log.id) }) {
                            Text("Удалить", color = AppPalette.AccentPeach)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(consumed: Int, goal: Int) {
    val percent = (consumed.toFloat() / goal.coerceAtLeast(1)).coerceIn(0f, 1f)
    Card(colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface), shape = RoundedCornerShape(22.dp), border = BorderStroke(1.dp, AppPalette.CardOutline)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Прогресс: ${(percent * 100).toInt()}%", color = AppPalette.AccentLilac)
            androidx.compose.material3.LinearProgressIndicator(progress = percent, modifier = Modifier.fillMaxWidth(), color = AppPalette.AccentMint, trackColor = AppPalette.CardOutline)
            Text("$consumed / $goal мл", color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

private fun formatLogTime(timestamp: Long): String {
    val time = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
    return time.format(DateTimeFormatter.ofPattern("dd MMM HH:mm"))
}

@Composable
private fun waterFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = AppPalette.TextPrimary,
    unfocusedTextColor = AppPalette.TextPrimary,
    cursorColor = AppPalette.AccentMint,
    focusedBorderColor = AppPalette.AccentMint,
    unfocusedBorderColor = AppPalette.CardOutline,
    focusedLabelColor = AppPalette.TextSecondary,
    unfocusedLabelColor = AppPalette.TextSecondary,
    focusedPlaceholderColor = AppPalette.TextSecondary,
    unfocusedPlaceholderColor = AppPalette.TextSecondary
)

