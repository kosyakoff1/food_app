package com.kosyakoff.foodapp.states

data class SelectedMealAndDietState(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int
)