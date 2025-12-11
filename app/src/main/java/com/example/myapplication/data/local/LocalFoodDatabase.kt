package com.example.myapplication.data.local

import com.example.myapplication.data.model.FoodItem

object LocalFoodDatabase {

    val sportsNutrition: List<FoodItem> = listOf(
        FoodItem(
            name = "Whey Protein Isolate",
            brand = "PureFuel",
            caloriesPer100 = 390,
            proteinPer100 = 84f,
            carbsPer100 = 5f,
            fatsPer100 = 2f,
            tags = listOf("lactose", "high-protein")
        ),
        FoodItem(
            name = "Plant Protein Blend",
            brand = "GreenMuscle",
            caloriesPer100 = 360,
            proteinPer100 = 78f,
            carbsPer100 = 8f,
            fatsPer100 = 6f,
            tags = listOf("vegan", "lactose-free", "high-protein")
        ),
        FoodItem(
            name = "Casein Protein",
            brand = "NightBuild",
            caloriesPer100 = 370,
            proteinPer100 = 77f,
            carbsPer100 = 10f,
            fatsPer100 = 4f,
            tags = listOf("slow-digesting")
        ),
        FoodItem(
            name = "Creatine Monohydrate",
            brand = "PowerLabs",
            caloriesPer100 = 0,
            proteinPer100 = 0f,
            carbsPer100 = 0f,
            fatsPer100 = 0f,
            tags = listOf("strength", "performance")
        ),
        FoodItem(
            name = "BCAA 2:1:1",
            brand = "AminoCore",
            caloriesPer100 = 200,
            proteinPer100 = 50f,
            carbsPer100 = 0f,
            fatsPer100 = 0f,
            tags = listOf("recovery", "intra-workout")
        ),
        FoodItem(
            name = "Omega-3 Fish Oil",
            brand = "ArcticPure",
            caloriesPer100 = 900,
            proteinPer100 = 0f,
            carbsPer100 = 0f,
            fatsPer100 = 100f,
            tags = listOf("health", "anti-inflammatory")
        ),
        FoodItem(
            name = "Electrolyte Hydration Mix",
            brand = "HydroBalance",
            caloriesPer100 = 150,
            proteinPer100 = 0f,
            carbsPer100 = 37f,
            fatsPer100 = 0f,
            tags = listOf("hydration")
        )
    )

    val wholeFoods: List<FoodItem> = listOf(
        FoodItem(
            name = "Chicken Breast",
            brand = "Organic Farm",
            caloriesPer100 = 165,
            proteinPer100 = 31f,
            carbsPer100 = 0f,
            fatsPer100 = 3.6f,
            tags = listOf("meat", "gluten-free")
        ),
        FoodItem(
            name = "Salmon Fillet",
            brand = "Nordic Sea",
            caloriesPer100 = 208,
            proteinPer100 = 20f,
            carbsPer100 = 0f,
            fatsPer100 = 13f,
            tags = listOf("omega-3", "gluten-free")
        ),
        FoodItem(
            name = "Quinoa",
            brand = "Andes Harvest",
            caloriesPer100 = 120,
            proteinPer100 = 4.4f,
            carbsPer100 = 21.3f,
            fatsPer100 = 1.9f,
            tags = listOf("vegan", "gluten-free", "complex-carbs")
        ),
        FoodItem(
            name = "Greek Yogurt 2%",
            brand = "Alpine Dairy",
            caloriesPer100 = 73,
            proteinPer100 = 10f,
            carbsPer100 = 3.6f,
            fatsPer100 = 2f,
            tags = listOf("lactose")
        ),
        FoodItem(
            name = "Cottage Cheese",
            brand = "FarmFresh",
            caloriesPer100 = 98,
            proteinPer100 = 11f,
            carbsPer100 = 3.4f,
            fatsPer100 = 4.3f,
            tags = listOf("lactose", "high-protein")
        ),
        FoodItem(
            name = "Avocado",
            brand = "Tropica",
            caloriesPer100 = 160,
            proteinPer100 = 2f,
            carbsPer100 = 8.5f,
            fatsPer100 = 14.7f,
            tags = listOf("vegan", "healthy-fats")
        ),
        FoodItem(
            name = "Buckwheat",
            brand = "Slavic Roots",
            caloriesPer100 = 110,
            proteinPer100 = 4.2f,
            carbsPer100 = 23f,
            fatsPer100 = 1f,
            tags = listOf("gluten-free", "complex-carbs")
        ),
        FoodItem(
            name = "Lentils",
            brand = "Pulse & Co",
            caloriesPer100 = 116,
            proteinPer100 = 9f,
            carbsPer100 = 20f,
            fatsPer100 = 0.4f,
            tags = listOf("vegan", "high-protein")
        ),
        FoodItem(
            name = "Tofu",
            brand = "SoyMaster",
            caloriesPer100 = 76,
            proteinPer100 = 8f,
            carbsPer100 = 1.9f,
            fatsPer100 = 4.8f,
            tags = listOf("vegan", "lactose-free", "plant-protein")
        ),
        FoodItem(
            name = "Egg Whites",
            brand = "ProteinFarm",
            caloriesPer100 = 52,
            proteinPer100 = 11f,
            carbsPer100 = 0.7f,
            fatsPer100 = 0.2f,
            tags = listOf("high-protein", "low-fat", "gluten-free")
        ),
        FoodItem(
            name = "Oats",
            brand = "Nordic Grains",
            caloriesPer100 = 389,
            proteinPer100 = 17f,
            carbsPer100 = 66f,
            fatsPer100 = 7f,
            tags = listOf("vegan", "complex-carbs")
        ),
        FoodItem(
            name = "Almond Butter",
            brand = "NutriBlend",
            caloriesPer100 = 614,
            proteinPer100 = 21f,
            carbsPer100 = 19f,
            fatsPer100 = 55f,
            tags = listOf("vegan", "healthy-fats", "lactose-free")
        )
    )

    val referenceArticles: Map<String, List<String>> = mapOf(
        "Protein Supplements" to listOf(
            "Whey protein быстро усваивается и подходит для приема утром и после тренировки.",
            "Казеин обеспечивает медленное высвобождение аминокислот и помогает уменьшить ночной катаболизм.",
            "Растительные смеси (горох, рис, конопля) подходят веганам и людям с непереносимостью лактозы."
        ),
        "Amino Acids" to listOf(
            "BCAA 2:1:1 поддерживают синтез белка и помогают уменьшить усталость.",
            "EAA особенно актуальны для людей с низким потреблением белка.",
            "L-Glutamine ускоряет восстановление ЖКТ после интенсивных тренировок."
        ),
        "Creatine" to listOf(
            "Креатин моногидрат - самая изученная форма, повышает силовые показатели и объем клеток.",
            "Прием 3-5 г ежедневно обеспечивает постоянное насыщение запасов.",
            "Важно соблюдать питьевой режим при приеме креатина."
        ),
        "Hydration" to listOf(
            "Баланс электролитов критичен при высоком потоотделении.",
            "Добавляйте 300-500 мл воды за 15 минут до тренировки.",
            "Используйте напитки с натрием и калием для долгих тренировок."
        )
    )
}




