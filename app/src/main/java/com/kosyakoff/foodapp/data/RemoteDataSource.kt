package com.kosyakoff.foodapp.data

import com.kosyakoff.foodapp.data.network.FoodRecipesApi
import com.kosyakoff.foodapp.models.FoodJoke
import com.kosyakoff.foodapp.models.FoodRecipes
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
) {

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipes> =
        foodRecipesApi.getRecipes(queries)

    suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipes> =
        foodRecipesApi.searchRecipes(searchQuery)

    suspend fun getFoodJoke(apiKey: String): Response<FoodJoke> =
        foodRecipesApi.getFoodJoke(apiKey)
}