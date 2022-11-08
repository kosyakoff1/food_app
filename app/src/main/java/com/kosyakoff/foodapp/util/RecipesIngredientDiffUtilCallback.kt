package com.kosyakoff.foodapp.util

import androidx.recyclerview.widget.DiffUtil
import com.kosyakoff.foodapp.models.ExtendedIngredients

object RecipesIngredientDiffUtilCallback : DiffUtil.ItemCallback<ExtendedIngredients>() {
    override fun areItemsTheSame(
        oldItem: ExtendedIngredients,
        newItem: ExtendedIngredients
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: ExtendedIngredients,
        newItem: ExtendedIngredients
    ): Boolean {
        return oldItem == newItem
    }
}