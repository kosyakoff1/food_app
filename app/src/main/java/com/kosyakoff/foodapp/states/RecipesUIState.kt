package com.kosyakoff.foodapp.states

import com.kosyakoff.foodapp.data.MealAndDietType
import com.kosyakoff.foodapp.models.FoodRecipes
import com.kosyakoff.foodapp.util.NetworkResult

data class RecipesUIState(
    val userMessages: List<UserMessage>,
    val mealAndDietType: MealAndDietType,
    val recipes: NetworkResult<FoodRecipes>
)