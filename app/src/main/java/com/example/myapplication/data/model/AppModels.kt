package com.example.myapplication.data.model

import kotlinx.serialization.Serializable
import java.util.UUID


data class AppState(
    val profiles: List<UserProfile> = emptyList(),
    val activeProfileId: String? = null,
    val diaryEntries: List<DiaryEntry> = emptyList(),
    val reminders: List<NutritionReminder> = emptyList(),
    val waterLogs: List<WaterLog> = emptyList(),
    val bodyMetrics: List<BodyMetric> = emptyList()
)


data class UserProfile(
    val id: String = UUID.randomUUID().toString(),
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val isVerified: Boolean = false,
    val heightCm: Int = 0,
    val weightKg: Float = 0f,
    val age: Int = 0,
    val gender: Gender = Gender.Other,
    val activityLevel: ActivityLevel = ActivityLevel.Sedentary,
    val goal: GoalType = GoalType.Maintain,
    val preferences: List<String> = emptyList(),
    val restrictions: List<String> = emptyList(),
    val activityDescription: String = "",
    val coachNotes: String = "",
    val waterGoalMl: Int = 2500
)

@Serializable
enum class Gender { Male, Female, Other }

@Serializable
enum class ActivityLevel(val multiplier: Double) {
    Sedentary(1.2),
    LightlyActive(1.375),
    ModeratelyActive(1.55),
    VeryActive(1.725),
    Athlete(1.9)
}

@Serializable
enum class GoalType(val calorieDelta: Int) {
    LoseFat(-400),
    Maintain(0),
    GainMuscle(300),
    Recomposition(-100)
}


data class DiaryEntry(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val timestamp: Long,
    val mealType: MealType,
    val productName: String,
    val quantity: Float,
    val unit: String = "g",
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val source: DiaryEntrySource = DiaryEntrySource.Manual
)

@Serializable
enum class MealType { Breakfast, Snack, Lunch, Dinner, Supplement }

@Serializable
enum class DiaryEntrySource { Manual, Database, Recommendation }


data class NutritionReminder(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val type: ReminderType,
    val hour: Int,
    val minute: Int,
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7),
    val enabled: Boolean = true,
    val notes: String = ""
)

@Serializable
enum class ReminderType { Meal, Supplement, Water }

data class WaterLog(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val timestamp: Long,
    val amountMl: Int
)

data class BodyMetric(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val dateMillis: Long,
    val weightKg: Float,
    val bodyFatPercent: Float? = null,
    val waistCm: Float? = null,
    val notes: String = ""
)

data class MacroTargets(
    val calories: Int,
    val protein: Int,
    val fats: Int,
    val carbs: Int
)


data class Recommendation(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val category: RecommendationCategory,
    val protein: Float,
    val carbs: Float,
    val fats: Float,
    val calories: Int,
    val tags: List<String> = emptyList()
)

@Serializable
enum class RecommendationCategory { SportsNutrition, WholeFood }


data class FoodItem(
    val name: String,
    val brand: String = "",
    val caloriesPer100: Int,
    val proteinPer100: Float,
    val carbsPer100: Float,
    val fatsPer100: Float,
    val tags: List<String> = emptyList(),
    val imageUrl: String = ""
)




