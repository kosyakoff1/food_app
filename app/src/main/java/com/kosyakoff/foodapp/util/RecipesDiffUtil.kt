package com.kosyakoff.foodapp.util

import androidx.recyclerview.widget.DiffUtil
import com.kosyakoff.foodapp.models.FoodRecipe

class RecipesDiffUtil(
    private val oldList: List<FoodRecipe>,
    private val newList: List<FoodRecipe>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}