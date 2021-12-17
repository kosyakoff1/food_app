package com.kosyakoff.foodapp.models

import com.google.gson.annotations.SerializedName

data class FoodRecipes(
    @SerializedName(value = "results")
    val results: List<FoodRecipe>
)
