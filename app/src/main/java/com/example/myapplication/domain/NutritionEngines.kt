package com.example.myapplication.domain

import com.example.myapplication.data.local.LocalFoodDatabase
import com.example.myapplication.data.model.ActivityLevel
import com.example.myapplication.data.model.Gender
import com.example.myapplication.data.model.GoalType
import com.example.myapplication.data.model.MacroTargets
import com.example.myapplication.data.model.Recommendation
import com.example.myapplication.data.model.RecommendationCategory
import com.example.myapplication.data.model.UserProfile
import kotlin.math.max
import kotlin.math.roundToInt

object MacroCalculator {

    fun calculate(profile: UserProfile?): MacroTargets {
        if (profile == null || profile.weightKg <= 0f || profile.heightCm <= 0 || profile.age <= 0) {
            return MacroTargets(
                calories = 2200,
                protein = 150,
                fats = 70,
                carbs = 250
            )
        }

        val bmr = calculateBmr(profile)
        val tdee = bmr * profile.activityLevel.multiplier
        val adjustedCalories = (tdee + profile.goal.calorieDelta).roundToInt().coerceAtLeast(1200)

        val proteinPerKg = when (profile.goal) {
            GoalType.GainMuscle -> 2.2
            GoalType.LoseFat -> 2.0
            GoalType.Recomposition -> 2.1
            GoalType.Maintain -> 1.8
        }

        val proteinGrams = max(110.0, profile.weightKg * proteinPerKg).roundToInt()
        val fatCalories = (adjustedCalories * 0.25)
        val fatGrams = (fatCalories / 9).roundToInt().coerceAtLeast(40)
        val carbCalories = adjustedCalories - (proteinGrams * 4) - (fatGrams * 9)
        val carbGrams = max(50.0, carbCalories / 4.0).roundToInt()

        return MacroTargets(
            calories = adjustedCalories,
            protein = proteinGrams,
            fats = fatGrams,
            carbs = carbGrams
        )
    }

    private fun calculateBmr(profile: UserProfile): Double {
        val base = 10 * profile.weightKg + 6.25 * profile.heightCm - 5 * profile.age
        return if (profile.gender == Gender.Female) base - 161 else base + 5
    }
}

class RecommendationEngine {

    fun buildRecommendations(
        profile: UserProfile?,
        macroTargets: MacroTargets
    ): List<Recommendation> {
        val safeProfile = profile ?: return fallbackRecommendations(macroTargets)

        val sports = LocalFoodDatabase.sportsNutrition
            .filter { item -> matchesPreferences(item.tags, safeProfile) }
            .map { food ->
                Recommendation(
                    title = food.name,
                    description = sportsDescription(food, safeProfile.goal),
                    category = RecommendationCategory.SportsNutrition,
                    protein = food.proteinPer100,
                    carbs = food.carbsPer100,
                    fats = food.fatsPer100,
                    calories = food.caloriesPer100,
                    tags = food.tags
                )
            }

        val wholeFoods = LocalFoodDatabase.wholeFoods
            .filter { item -> matchesPreferences(item.tags, safeProfile) }
            .map { food ->
                Recommendation(
                    title = food.name,
                    description = wholeFoodsDescription(food, safeProfile),
                    category = RecommendationCategory.WholeFood,
                    protein = food.proteinPer100,
                    carbs = food.carbsPer100,
                    fats = food.fatsPer100,
                    calories = food.caloriesPer100,
                    tags = food.tags
                )
            }

        val prioritized = (sports + wholeFoods)
            .sortedByDescending { it.protein }
            .take(10)

        return if (prioritized.isNotEmpty()) prioritized else fallbackRecommendations(macroTargets)
    }

    private fun matchesPreferences(tags: List<String>, profile: UserProfile): Boolean {
        val restrictions = profile.restrictions.map { it.lowercase() }
        if (restrictions.contains("лактоза") || restrictions.contains("lactose")) {
            if (tags.any { it.contains("lactose") }) return false
        }
        if (restrictions.contains("веган") || profile.preferences.any { it.contains("vegan", ignoreCase = true) }) {
            if (!tags.contains("vegan") && !tags.contains("plant-protein")) return false
        }
        if (restrictions.contains("вегетариан") || profile.preferences.any { it.contains("vegetarian", ignoreCase = true) }) {
            if (tags.contains("meat")) return false
        }
        if (restrictions.contains("gluten")) {
            if (!tags.contains("gluten-free") && tags.contains("gluten")) return false
        }
        return true
    }

    private fun sportsDescription(food: com.example.myapplication.data.model.FoodItem, goal: GoalType): String {
        return when {
            food.tags.contains("slow-digesting") -> "Ночной прием для стабильного притока аминокислот."
            food.tags.contains("recovery") -> "Ускоряет восстановление и уменьшает усталость."
            food.tags.contains("hydration") -> "Стабилизирует баланс электролитов и воды."
            goal == GoalType.GainMuscle && food.proteinPer100 > 70 -> "Высокая концентрация белка для набора массы."
            goal == GoalType.LoseFat && food.fatsPer100 < 5 -> "Низкое содержание жиров для дефицита калорий."
            else -> "Сбалансированная поддержка спортивного питания."
        }
    }

    private fun wholeFoodsDescription(food: com.example.myapplication.data.model.FoodItem, profile: UserProfile): String {
        return when {
            food.tags.contains("complex-carbs") -> "Медленные углеводы для стабильной энергии."
            food.tags.contains("healthy-fats") -> "Источник полезных жиров и жирорастворимых витаминов."
            food.tags.contains("high-protein") -> "Удобный белковый прием для ${profile.goal.name.lowercase()}."
            food.name.contains("Salmon", ignoreCase = true) -> "Омега-3 поддерживает гормональный фон."
            food.name.contains("Chicken", ignoreCase = true) -> "Классический источник постного белка."
            else -> "Подходит под ваши предпочтения и ограничения."
        }
    }

    private fun fallbackRecommendations(targets: MacroTargets): List<Recommendation> {
        return listOf(
            Recommendation(
                title = "Завтрак: овсянка + белок",
                description = "50 г овсянки, 200 мл растительного молока и 30 г протеина.",
                category = RecommendationCategory.WholeFood,
                protein = 28f,
                carbs = 45f,
                fats = 9f,
                calories = 420,
                tags = listOf("high-protein", "complex-carbs")
            ),
            Recommendation(
                title = "Вечерний казеин",
                description = "30 г казеина перед сном для восстановления.",
                category = RecommendationCategory.SportsNutrition,
                protein = 24f,
                carbs = 3f,
                fats = 1f,
                calories = 120,
                tags = listOf("slow-digesting")
            ),
            Recommendation(
                title = "Салат с лососем и киноа",
                description = "Поддерживает норму жиров и омега-3 (цель: ${targets.calories} ккал).",
                category = RecommendationCategory.WholeFood,
                protein = 32f,
                carbs = 35f,
                fats = 18f,
                calories = 520,
                tags = listOf("omega-3", "balanced")
            )
        )
    }
}



