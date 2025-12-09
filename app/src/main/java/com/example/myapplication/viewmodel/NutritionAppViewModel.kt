package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.auth.FakeSmsService
import com.example.myapplication.data.local.LocalFoodDatabase
import com.example.myapplication.data.model.ActivityLevel
import com.example.myapplication.data.model.AppState
import com.example.myapplication.data.model.BodyMetric
import com.example.myapplication.data.model.DiaryEntry
import com.example.myapplication.data.model.DiaryEntrySource
import com.example.myapplication.data.model.Gender
import com.example.myapplication.data.model.GoalType
import com.example.myapplication.data.model.MacroTargets
import com.example.myapplication.data.model.MealType
import com.example.myapplication.data.model.NutritionReminder
import com.example.myapplication.data.model.ReminderType
import com.example.myapplication.data.model.Recommendation
import com.example.myapplication.data.model.UserProfile
import com.example.myapplication.data.model.WaterLog
import com.example.myapplication.data.repository.NutritionStateRepository
import com.example.myapplication.domain.MacroCalculator
import com.example.myapplication.domain.RecommendationEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

data class AuthUiState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val enteredCode: String = "",
    val generatedCode: String? = null,
    val isCodeSent: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false
)

data class DashboardUiState(
    val activeProfile: UserProfile? = null,
    val macroTargets: MacroTargets = MacroCalculator.calculate(null),
    val todayDiary: List<DiaryEntry> = emptyList(),
    val waterConsumed: Int = 0,
    val waterGoal: Int = 2500,
    val recommendations: List<Recommendation> = emptyList()
)

class NutritionAppViewModel(
    private val repository: NutritionStateRepository,
    private val recommendationEngine: RecommendationEngine,
    private val smsService: FakeSmsService
) : ViewModel() {

    val appState: StateFlow<AppState> = repository.appStateFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppState()
    )

    private val _authState = MutableStateFlow(AuthUiState())
    val authState: StateFlow<AuthUiState> = _authState

    val dashboardState: StateFlow<DashboardUiState> = appState
        .map { it.toDashboardState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState()
        )

    private fun AppState.toDashboardState(): DashboardUiState {
        val profile = profiles.find { it.id == activeProfileId }
        val macroTargets = MacroCalculator.calculate(profile)
        val todayRange = currentDayRange()
        val diary = diaryEntries.filter { entry ->
            entry.userId == profile?.id && entry.timestamp in todayRange.first..todayRange.second
        }
        val water = waterLogs.filter { log ->
            log.userId == profile?.id && log.timestamp in todayRange.first..todayRange.second
        }.sumOf { it.amountMl }

        return DashboardUiState(
            activeProfile = profile,
            macroTargets = macroTargets,
            todayDiary = diary,
            waterConsumed = water,
            waterGoal = profile?.waterGoalMl ?: 2500,
            recommendations = recommendationEngine.buildRecommendations(profile, macroTargets)
        )
    }

    fun requestSmsCode(fullName: String, email: String, phone: String) {
        val sanitizedPhone = phone.filter { it.isDigit() || it == '+' }
        val sanitizedEmail = email.trim()
        if (sanitizedEmail.isBlank() || sanitizedPhone.length < 10) {
            _authState.value = _authState.value.copy(error = "Введите корректные email и телефон")
            return
        }
        val code = (100000..999999).random().toString()
        smsService.sendCode(sanitizedPhone, code)
        _authState.value = AuthUiState(
            fullName = fullName.ifBlank { "Новый пользователь" },
            email = sanitizedEmail,
            phone = sanitizedPhone,
            generatedCode = code,
            isCodeSent = true
        )
    }

    fun verifyCode(input: String) {
        val state = _authState.value
        if (!state.isCodeSent || state.generatedCode == null) {
            _authState.value = state.copy(error = "Сначала запросите SMS-код")
            return
        }
        if (state.generatedCode != input.trim()) {
            _authState.value = state.copy(error = "Неверный код")
            return
        }
        viewModelScope.launch {
            val existing = appState.value.profiles.find {
                it.email.equals(state.email, ignoreCase = true) || it.phone == state.phone
            }
            val profile = (existing ?: UserProfile(
                fullName = state.fullName,
                email = state.email,
                phone = state.phone
            )).copy(isVerified = true)
            repository.upsertProfile(profile, setActive = true)
            _authState.value = AuthUiState()
        }
    }

    fun updateProfile(
        fullName: String,
        age: Int,
        height: Int,
        weight: Float,
        gender: Gender,
        activity: ActivityLevel,
        goal: GoalType,
        preferences: List<String>,
        restrictions: List<String>,
        waterGoal: Int
    ) {
        val profile = dashboardState.value.activeProfile
        val base = profile ?: UserProfile(
            fullName = fullName,
            email = authState.value.email,
            phone = authState.value.phone,
            isVerified = true
        )
        val updated = base.copy(
            fullName = fullName,
            age = age,
            heightCm = height,
            weightKg = weight,
            gender = gender,
            activityLevel = activity,
            goal = goal,
            preferences = preferences,
            restrictions = restrictions,
            waterGoalMl = waterGoal
        )
        viewModelScope.launch {
            repository.upsertProfile(updated, setActive = true)
        }
    }

    fun switchProfile(profileId: String) {
        viewModelScope.launch {
            repository.setActiveProfile(profileId)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearActiveProfile()
        }
    }

    fun addDiaryEntry(
        mealType: MealType,
        productName: String,
        quantity: Float,
        unit: String,
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float,
        source: DiaryEntrySource = DiaryEntrySource.Manual
    ) {
        val profile = dashboardState.value.activeProfile ?: return
        val entry = DiaryEntry(
            userId = profile.id,
            timestamp = System.currentTimeMillis(),
            mealType = mealType,
            productName = productName,
            quantity = quantity,
            unit = unit,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            source = source
        )
        viewModelScope.launch {
            repository.addDiaryEntry(entry)
        }
    }

    fun removeDiaryEntry(entryId: String) {
        viewModelScope.launch {
            repository.deleteDiaryEntry(entryId)
        }
    }

    fun addReminder(
        title: String,
        type: ReminderType,
        hour: Int,
        minute: Int,
        daysOfWeek: List<Int>,
        notes: String
    ) {
        val profile = dashboardState.value.activeProfile ?: return
        val reminder = NutritionReminder(
            userId = profile.id,
            title = title,
            type = type,
            hour = hour,
            minute = minute,
            daysOfWeek = daysOfWeek,
            notes = notes
        )
        viewModelScope.launch {
            repository.addReminder(reminder)
        }
    }

    fun toggleReminder(reminderId: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleReminder(reminderId, enabled)
        }
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            repository.deleteReminder(reminderId)
        }
    }

    fun addWater(amountMl: Int) {
        val profile = dashboardState.value.activeProfile ?: return
        val log = WaterLog(
            userId = profile.id,
            timestamp = System.currentTimeMillis(),
            amountMl = amountMl
        )
        viewModelScope.launch {
            repository.addWaterLog(log)
        }
    }

    fun deleteWaterLog(logId: String) {
        viewModelScope.launch {
            repository.deleteWaterLog(logId)
        }
    }

    fun updateWaterGoal(goal: Int) {
        val profile = dashboardState.value.activeProfile ?: return
        viewModelScope.launch {
            repository.upsertProfile(profile.copy(waterGoalMl = goal))
        }
    }

    fun addBodyMetric(weight: Float, bodyFat: Float?, waist: Float?, notes: String = "") {
        val profile = dashboardState.value.activeProfile ?: return
        val metric = BodyMetric(
            userId = profile.id,
            dateMillis = System.currentTimeMillis(),
            weightKg = weight,
            bodyFatPercent = bodyFat,
            waistCm = waist?.toFloat(),
            notes = notes
        )
        viewModelScope.launch {
            repository.addBodyMetric(metric)
        }
    }

    fun quickAddFromFoodDatabase(foodName: String, mealType: MealType, amountGrams: Int) {
        val food = (LocalFoodDatabase.sportsNutrition + LocalFoodDatabase.wholeFoods)
            .find { it.name == foodName } ?: return
        val factor = amountGrams / 100f
        addDiaryEntry(
            mealType = mealType,
            productName = food.name,
            quantity = amountGrams.toFloat(),
            unit = "g",
            calories = (food.caloriesPer100 * factor).toInt(),
            protein = food.proteinPer100 * factor,
            carbs = food.carbsPer100 * factor,
            fat = food.fatsPer100 * factor,
            source = DiaryEntrySource.Database
        )
    }

    fun updateAuthName(name: String) {
        _authState.value = _authState.value.copy(fullName = name)
    }

    fun updateAuthEmail(email: String) {
        _authState.value = _authState.value.copy(email = email)
    }

    fun updateAuthPhone(phone: String) {
        _authState.value = _authState.value.copy(phone = phone)
    }

    fun updateEnteredCode(code: String) {
        _authState.value = _authState.value.copy(enteredCode = code)
    }

    private fun currentDayRange(): Pair<Long, Long> {
        val now = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()
        val start = now.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = now.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusNanos(1).toInstant().toEpochMilli()
        return start to end
    }
}

class NutritionAppViewModelFactory(
    private val repository: NutritionStateRepository,
    private val recommendationEngine: RecommendationEngine,
    private val smsService: FakeSmsService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NutritionAppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NutritionAppViewModel(repository, recommendationEngine, smsService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

