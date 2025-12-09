package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.NutritionProduct
import com.example.myapplication.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ProductViewModel : ViewModel() {
    
    private val apiService = RetrofitClient.apiService
    
    private val _products = MutableStateFlow<List<NutritionProduct>>(emptyList())
    val products: StateFlow<List<NutritionProduct>> = _products.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val response = apiService.getProducts(limit = 30)
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse != null) {
                        _products.value = productResponse.products
                    } else {
                        _error.value = "No products found"
                    }
                } else {
                    _error.value = "Failed to load products: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchProducts(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            // Add small delay to prevent rapid API calls
            delay(300)
            
            try {
                val response = apiService.searchProducts(query, limit = 30)
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse != null) {
                        _products.value = productResponse.products
                    } else {
                        _error.value = "No products found for '$query'"
                    }
                } else {
                    _error.value = "Failed to search products: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadSportsNutritionProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val response = apiService.getSportsNutritionProducts(limit = 30)
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse != null) {
                        _products.value = productResponse.products
                    } else {
                        _error.value = "No sports nutrition products found"
                    }
                } else {
                    _error.value = "Failed to load sports nutrition products: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadProteinProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val response = apiService.searchProducts("protein whey casein", limit = 30)
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse != null) {
                        _products.value = productResponse.products
                    } else {
                        _error.value = "No protein products found"
                    }
                } else {
                    _error.value = "Failed to load protein products: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun filterProteins(filter: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val searchQuery = when (filter) {
                    "All Proteins" -> "protein whey casein"
                    "Whey Protein" -> "whey protein"
                    "Casein" -> "casein protein"
                    "Plant Protein" -> "plant protein vegan soy"
                    "Mass Gainers" -> "mass gainer weight gain"
                    else -> "protein whey casein"
                }
                
                val response = apiService.searchProducts(searchQuery, limit = 30)
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse != null) {
                        _products.value = productResponse.products
                    } else {
                        _error.value = "No products found for $filter"
                    }
                } else {
                    _error.value = "Failed to filter products: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
