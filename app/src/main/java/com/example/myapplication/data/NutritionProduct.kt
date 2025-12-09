package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

data class NutritionProduct(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("brand")
    val brand: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("image")
    val image: String?,
    
    @SerializedName("rating")
    val rating: Double,
    
    @SerializedName("stock")
    val stock: Int,
    
    @SerializedName("nutrition_facts")
    val nutritionFacts: NutritionFacts?
)

data class NutritionFacts(
    @SerializedName("calories")
    val calories: Int?,
    
    @SerializedName("protein")
    val protein: Double?,
    
    @SerializedName("carbs")
    val carbs: Double?,
    
    @SerializedName("fat")
    val fat: Double?,
    
    @SerializedName("fiber")
    val fiber: Double?,
    
    @SerializedName("sugar")
    val sugar: Double?
)

data class ProductResponse(
    @SerializedName("products")
    val products: List<NutritionProduct>,
    
    @SerializedName("total")
    val total: Int,
    
    @SerializedName("skip")
    val skip: Int,
    
    @SerializedName("limit")
    val limit: Int
)



