package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.data.auth.FakeSmsService
import com.example.myapplication.data.repository.NutritionStateRepository
import com.example.myapplication.domain.RecommendationEngine

class AppContainer(context: Context) {
    val stateRepository = NutritionStateRepository(context)
    val recommendationEngine = RecommendationEngine()
    val smsService = FakeSmsService(context)
}



