package com.example.myapplication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.ActivityLevel
import com.example.myapplication.data.model.Gender
import com.example.myapplication.data.model.GoalType
import com.example.myapplication.ui.theme.AppPalette
import com.example.myapplication.viewmodel.NutritionAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: NutritionAppViewModel) {
    val appState by viewModel.appState.collectAsState()
    val profile = appState.profiles.find { it.id == appState.activeProfileId }
    var fullName by remember { mutableStateOf(profile?.fullName ?: "") }
    var age by remember { mutableStateOf(profile?.age?.toString() ?: "") }
    var height by remember { mutableStateOf(profile?.heightCm?.toString() ?: "") }
    var weight by remember { mutableStateOf(profile?.weightKg?.toString() ?: "") }
    var gender by remember { mutableStateOf(profile?.gender ?: Gender.Other) }
    var activity by remember { mutableStateOf(profile?.activityLevel ?: ActivityLevel.Sedentary) }
    var goal by remember { mutableStateOf(profile?.goal ?: GoalType.Maintain) }
    var preferences by remember { mutableStateOf(profile?.preferences ?: emptyList()) }
    var restrictions by remember { mutableStateOf(profile?.restrictions ?: emptyList()) }
    var waterGoal by remember { mutableStateOf(profile?.waterGoalMl?.toString() ?: "2500") }

    LaunchedEffect(profile?.id) {
        if (profile != null) {
            fullName = profile.fullName
            age = profile.age.takeIf { it > 0 }?.toString() ?: ""
            height = profile.heightCm.takeIf { it > 0 }?.toString() ?: ""
            weight = profile.weightKg.takeIf { it > 0f }?.toString() ?: ""
            gender = profile.gender
            activity = profile.activityLevel
            goal = profile.goal
            preferences = profile.preferences
            restrictions = profile.restrictions
            waterGoal = profile.waterGoalMl.toString()
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPalette.BackgroundGradient)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Профиль пользователя", color = AppPalette.TextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Укажите данные, чтобы расчеты были точными.", color = AppPalette.TextSecondary)

        Card(colors = CardDefaults.cardColors(containerColor = AppPalette.CardSurface), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, AppPalette.CardOutline)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Имя и фамилия") }, modifier = Modifier.fillMaxWidth(), colors = profileFieldColors())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Возраст") }, modifier = Modifier.weight(1f), colors = profileFieldColors())
                    OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Рост, см") }, modifier = Modifier.weight(1f), colors = profileFieldColors())
                    OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Вес, кг") }, modifier = Modifier.weight(1f), colors = profileFieldColors())
                }
                ChoiceRow("Пол", Gender.values().toList(), gender) { gender = it }
                ChoiceRow("Активность", ActivityLevel.values().toList(), activity) { activity = it }
                ChoiceRow("Цель", GoalType.values().toList(), goal) { goal = it }
                ChipGroup(
                    title = "Предпочтения",
                    options = listOf("Вегетарианство", "Веганство", "Без лактозы", "Без глютена", "Кето"),
                    selected = preferences,
                    onToggle = { option ->
                        preferences = if (preferences.contains(option)) preferences - option else preferences + option
                    }
                )
                ChipGroup(
                    title = "Ограничения",
                    options = listOf("Лактоза", "Глютен", "Орехи", "Яйца", "Соя"),
                    selected = restrictions,
                    onToggle = { option ->
                        restrictions = if (restrictions.contains(option)) restrictions - option else restrictions + option
                    }
                )
                OutlinedTextField(
                    value = waterGoal,
                    onValueChange = { waterGoal = it },
                    label = { Text("Суточная цель по воде, мл") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = profileFieldColors()
                )
                Button(
                    onClick = {
                        viewModel.updateProfile(
                            fullName = fullName,
                            age = age.toIntOrNull() ?: 0,
                            height = height.toIntOrNull() ?: 0,
                            weight = weight.toFloatOrNull() ?: 0f,
                            gender = gender,
                            activity = activity,
                            goal = goal,
                            preferences = preferences,
                            restrictions = restrictions,
                            waterGoal = waterGoal.toIntOrNull() ?: 2500
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppPalette.AccentMint),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Сохранить профиль", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Профили на устройстве", color = AppPalette.TextPrimary, style = MaterialTheme.typography.titleMedium)
        appState.profiles.forEach { user ->
            Card(
                colors = CardDefaults.cardColors(containerColor = if (user.id == profile?.id) AppPalette.CardAltSurface else AppPalette.CardSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AppPalette.CardOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(user.fullName, color = AppPalette.TextPrimary, fontWeight = FontWeight.Bold)
                    Text("${user.email} | ${user.phone}", color = AppPalette.TextSecondary, style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                            onClick = { viewModel.switchProfile(user.id) },
                    colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.id == profile?.id) AppPalette.AccentMint else AppPalette.AccentLilac
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(if (user.id == profile?.id) "Активен" else "Сделать активным", color = Color.White)
                        }
                    }
                }
            }
        }

        Button(
            onClick = viewModel::logout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AppPalette.AccentPeach),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Добавить новый профиль / выйти", color = Color.White)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> ChoiceRow(
    title: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, color = Color.White)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selected,
                    onClick = { onSelected(option) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppPalette.ChipSelected,
                        containerColor = AppPalette.CardSurface,
                        selectedLabelColor = AppPalette.TextPrimary,
                        labelColor = AppPalette.TextSecondary
                    ),
                    label = {
            Text(
                            option.toString(),
                            color = if (option == selected) Color(0xFF4CAF50) else Color.White
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipGroup(
    title: String,
    options: List<String>,
    selected: List<String>,
    onToggle: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, color = Color.White)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selected.contains(option),
                    onClick = { onToggle(option) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppPalette.ChipSelected,
                        containerColor = AppPalette.CardSurface,
                        selectedLabelColor = AppPalette.TextPrimary,
                        labelColor = AppPalette.TextSecondary
                    ),
                    label = {
            Text(
                            option,
                            color = if (selected.contains(option)) Color(0xFF4CAF50) else Color.White
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun profileFieldColors() = OutlinedTextFieldDefaults.colors(
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
