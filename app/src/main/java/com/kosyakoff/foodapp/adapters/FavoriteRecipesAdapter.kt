package com.kosyakoff.foodapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kosyakoff.foodapp.databinding.FavoriteRecipesRowLayoutBinding
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.util.RecipesDiffUtilCallback

class FavoriteRecipesAdapter :
    ListAdapter<FoodRecipe, FavoriteRecipesAdapter.MyViewHolder>(RecipesDiffUtilCallback) {

    class MyViewHolder(private val binding: FavoriteRecipesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: FoodRecipe) {
            binding.recipe = recipe
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder = MyViewHolder(
                FavoriteRecipesRowLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder.from(parent)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) =
        holder.bind(currentList[position])
}