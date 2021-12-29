package com.kosyakoff.foodapp.util

import androidx.recyclerview.widget.DiffUtil
import com.kosyakoff.foodapp.models.FoodRecipe

object RecipesDiffUtilCallback : DiffUtil.ItemCallback<FoodRecipe>() {
    override fun areItemsTheSame(
        oldItem: FoodRecipe,
        newItem: FoodRecipe
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: FoodRecipe,
        newItem: FoodRecipe
    ): Boolean {
        return oldItem == newItem
    }
}