package com.kosyakoff.foodapp.states

import com.kosyakoff.foodapp.models.FoodRecipe

data class DetailsUIState(
    val currentRecipe: FoodRecipe,
    val isFavored: Boolean,
    val userMessages: List<UserMessage>
) {
}