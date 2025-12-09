package com.example.myapplication.network

import com.example.myapplication.data.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 30,
        @Query("skip") skip: Int = 0
    ): Response<ProductResponse>
    
    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("limit") limit: Int = 30
    ): Response<ProductResponse>
    
    @GET("products/category/supplements")
    suspend fun getSupplementProducts(
        @Query("limit") limit: Int = 30
    ): Response<ProductResponse>
    
    @GET("products/search")
    suspend fun getSportsNutritionProducts(
        @Query("q") query: String = "protein whey bcaa creatine supplement nutrition fitness",
        @Query("limit") limit: Int = 30
    ): Response<ProductResponse>
}
