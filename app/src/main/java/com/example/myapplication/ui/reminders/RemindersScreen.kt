package com.example.myapplication.ui.reminders

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.NutritionReminder
import com.example.myapplication.data.model.ReminderType
import com.example.myapplication.ui.theme.AppPalette
import com.example.myapplication.viewmodel.NutritionAppViewModel

@Composable
fun RemindersScreen(viewModel: NutritionAppViewModel) {
    val appState by viewModel.appState.collectAsState()
    val profile = appState.profiles.find { it.id == appState.activeProfileId }
    val reminders = appState.reminders.filter { it.userId == profile?.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPalette.BackgroundGradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Напоминания", color = AppPalette.TextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Настройте расписание приемов пищи, добавок и воды.", color = AppPalette.TextSecondary)

        ReminderForm(onCreate = { title, type, hour, minute, days, notes ->
            viewModel.addReminder(title, type, hour, minute, days, notes)
        })

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f, fill = false)) {
            items(reminders, key = { it.id }) { reminder ->
                ReminderCard(
                    reminder = reminder,
                    onToggle = { viewModel.toggleReminder(reminder.id, it) },
                    onDelete = { viewModel.deleteReminder(reminder.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderForm(
    onCreate: (String, ReminderType, Int, Int, List<Int>, String) -> Unit
) {
    var title by remember { mutableStateOf("Основной прием пищи") }
    var type by remember { mutableStateOf(ReminderType.Meal) }
    var hour by remember { mutableStateOf("09") }
    var minute by remember { mutableStateOf("00") }
    var notes by remember { mutableStateOf("Сложные углеводы + белок") }
    val days = remember { mutableStateListOf(1, 2, 3, 4, 5, 6, 7) }
    var typeSelector by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, AppPalette.CardOutline)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Новый напоминатель", color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Название") }, modifier = Modifier.fillMaxWidth(), colors = reminderFieldColors())
            TextButton(onClick = { typeSelector = true }) {
                Text("Тип: ${type.name}", color = AppPalette.TextPrimary)
            }
            DropdownMenu(expanded = typeSelector, onDismissRequest = { typeSelector = false }) {
                ReminderType.values().forEach {
                    DropdownMenuItem(
                        text = { Text(it.name, color = AppPalette.TextPrimary) },
                        onClick = {
                            type = it
                            typeSelector = false
                        }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = hour, onValueChange = { hour = it.take(2) }, label = { Text("Часы") }, modifier = Modifier.weight(1f), colors = reminderFieldColors())
                OutlinedTextField(value = minute, onValueChange = { minute = it.take(2) }, label = { Text("Минуты") }, modifier = Modifier.weight(1f), colors = reminderFieldColors())
            }
            DaysSelector(selected = days, onToggle = { day ->
                if (days.contains(day)) days.remove(day) else days.add(day)
            })
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Заметка") }, modifier = Modifier.fillMaxWidth(), colors = reminderFieldColors())
            Button(
                onClick = {
                    onCreate(
                        title,
                        type,
                        hour.toIntOrNull() ?: 8,
                        minute.toIntOrNull() ?: 0,
                        days.toList(),
                        notes
                    )
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
private fun DaysSelector(selected: List<Int>, onToggle: (Int) -> Unit) {
    val dayLabels = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        dayLabels.forEachIndexed { index, label ->
            val day = index + 1
            val isSelected = selected.contains(day)
            TextButton(onClick = { onToggle(day) }, shape = RoundedCornerShape(50)) {
                Text(label, color = if (isSelected) AppPalette.AccentLilac else AppPalette.TextSecondary)
            }
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: NutritionReminder,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, AppPalette.CardOutline)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(reminder.title, color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
                    Text("${reminder.hour.toString().padStart(2, '0')}:${reminder.minute.toString().padStart(2, '0')}", color = AppPalette.AccentLilac)
                }
                Switch(
                    checked = reminder.enabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AppPalette.AccentMint,
                        uncheckedThumbColor = AppPalette.TextMuted
                    )
                )
            }
            Text("Тип: ${reminder.type.name}", color = AppPalette.TextSecondary, style = MaterialTheme.typography.bodySmall)
            Text("Дни: ${reminder.daysOfWeek.sorted().joinToString(", ")}", color = AppPalette.TextSecondary)
            Text(reminder.notes, color = AppPalette.TextPrimary)
            TextButton(onClick = onDelete) { Text("Удалить", color = AppPalette.AccentPeach) }
        }
    }
}

@Composable
private fun reminderFieldColors() = OutlinedTextFieldDefaults.colors(
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

