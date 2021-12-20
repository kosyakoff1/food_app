package com.kosyakoff.foodapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kosyakoff.foodapp.databinding.RecipesRowLayoutBinding
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.models.FoodRecipes
import com.kosyakoff.foodapp.util.RecipesDiffUtil

//TODO better change it to ListAdaper and asycnDiffUtil
class RecipesAdapter : RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {

    private var recipes = emptyList<FoodRecipe>()

    class MyViewHolder(private val binding: RecipesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: FoodRecipe) {
            binding.recipe = recipe
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder = MyViewHolder(
                RecipesRowLayoutBinding.inflate(
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
        holder.bind(recipes[position])

    fun setData(newData: FoodRecipes) {
        val recipesDiffUtil = RecipesDiffUtil(recipes, newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        diffUtilResult.dispatchUpdatesTo(this)
        recipes = newData.results

    }

    override fun getItemCount(): Int = recipes.size
}