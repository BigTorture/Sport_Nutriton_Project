package com.example.myapplication.ui.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.BodyMetric
import com.example.myapplication.ui.theme.AppPalette
import com.example.myapplication.viewmodel.NutritionAppViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ReportsScreen(viewModel: NutritionAppViewModel) {
    val appState by viewModel.appState.collectAsState()
    val profile = appState.profiles.find { it.id == appState.activeProfileId }
    val metrics = appState.bodyMetrics.filter { it.userId == profile?.id }.sortedBy { it.dateMillis }
    val diary = appState.diaryEntries.filter { it.userId == profile?.id }
    val lastWeekCalories = diary.filter { it.timestamp >= System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000 }
        .groupBy { dayFormatter(it.timestamp) }
        .mapValues { (_, entries) -> entries.sumOf { it.calories } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPalette.BackgroundGradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Графики и отчёты", color = AppPalette.TextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Следите за изменениями веса и соблюдением плана.", color = AppPalette.TextSecondary)

        MetricsForm(onSubmit = { weight, bodyFat, waist ->
            viewModel.addBodyMetric(weight, bodyFat, waist)
        })

        if (metrics.isNotEmpty()) {
            TrendCard(metrics = metrics)
        }

        if (lastWeekCalories.isNotEmpty()) {
            ComplianceCard(lastWeekCalories)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(metrics.reversed()) { metric ->
                MetricRow(metric)
            }
        }
    }
}

@Composable
private fun MetricsForm(onSubmit: (Float, Float?, Float?) -> Unit) {
    var weight by remember { mutableStateOf("") }
    var bodyFat by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    Card(colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, AppPalette.CardOutline)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Добавить измерение", color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Вес, кг") }, modifier = Modifier.weight(1f), colors = reportFieldColors())
                OutlinedTextField(value = bodyFat, onValueChange = { bodyFat = it }, label = { Text("% жира") }, modifier = Modifier.weight(1f), colors = reportFieldColors())
                OutlinedTextField(value = waist, onValueChange = { waist = it }, label = { Text("Талия, см") }, modifier = Modifier.weight(1f), colors = reportFieldColors())
            }
            Button(
                onClick = {
                    onSubmit(weight.toFloatOrNull() ?: return@Button, bodyFat.toFloatOrNull(), waist.toFloatOrNull())
                    weight = ""
                    bodyFat = ""
                    waist = ""
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AppPalette.AccentMint),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Сохранить", color = Color.White)
            }
        }
    }
}

@Composable
private fun TrendCard(metrics: List<BodyMetric>) {
    val weights = metrics.map { it.weightKg }
    val min = weights.minOrNull() ?: 0f
    val max = weights.maxOrNull() ?: 0f
    Card(colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, AppPalette.CardOutline)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Динамика веса", color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)) {
                if (weights.size < 2) return@Canvas
                val path = Path()
                weights.forEachIndexed { index, value ->
                    val x = size.width * index / (weights.lastIndex)
                    val ratio = if (max - min == 0f) 0.5f else (value - min) / (max - min)
                    val y = size.height - ratio * size.height
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, color = AppPalette.AccentMint, style = Stroke(width = 6f))
            }
            Text("Мин: ${"%.1f".format(min)} кг • Макс: ${"%.1f".format(max)} кг", color = AppPalette.TextSecondary)
        }
    }
}

@Composable
private fun ComplianceCard(values: Map<String, Int>) {
    Card(colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface), shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, AppPalette.CardOutline)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Последние 7 дней", color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
            values.entries.sortedBy { it.key }.forEach { (day, calories) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(day, color = AppPalette.TextSecondary)
                    Text("$calories ккал", color = AppPalette.TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun MetricRow(metric: BodyMetric) {
    Card(colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, AppPalette.CardOutline)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(dayFormatter(metric.dateMillis), color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
            Text("Вес: ${metric.weightKg} кг", color = AppPalette.TextPrimary)
            metric.bodyFatPercent?.let { Text("Жир: $it %", color = AppPalette.TextSecondary) }
            metric.waistCm?.let { Text("Талия: $it см", color = AppPalette.TextSecondary) }
        }
    }
}

private fun dayFormatter(timestamp: Long): String {
    val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(DateTimeFormatter.ofPattern("dd MMM"))
}

@Composable
private fun reportFieldColors() = OutlinedTextFieldDefaults.colors(
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

