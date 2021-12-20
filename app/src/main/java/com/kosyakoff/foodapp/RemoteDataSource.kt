package com.kosyakoff.foodapp

import com.kosyakoff.foodapp.models.FoodRecipes
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
) {

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipes> =
        foodRecipesApi.getRecipes(queries)
}