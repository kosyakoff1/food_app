package com.kosyakoff.foodapp.util

import androidx.recyclerview.widget.DiffUtil
import com.kosyakoff.foodapp.models.ExtendedIngredient

object RecipesIngredientDiffUtilCallback : DiffUtil.ItemCallback<ExtendedIngredient>() {
    override fun areItemsTheSame(
        oldItem: ExtendedIngredient,
        newItem: ExtendedIngredient
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: ExtendedIngredient,
        newItem: ExtendedIngredient
    ): Boolean {
        return oldItem == newItem
    }
}