package com.kosyakoff.foodapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kosyakoff.foodapp.databinding.IngredientsRowLayoutBinding
import com.kosyakoff.foodapp.models.ExtendedIngredient
import com.kosyakoff.foodapp.util.Constants.Companion.CDN_URL
import com.kosyakoff.foodapp.util.RecipesIngredientDiffUtilCallback
import java.util.*

class RecipeIngredientsAdapter :
    ListAdapter<ExtendedIngredient, RecipeIngredientsAdapter.ViewHolder>(
        RecipesIngredientDiffUtilCallback
    ) {

    class ViewHolder(private val binding: IngredientsRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ingredient: ExtendedIngredient) {
            binding.apply {
                ingredientImageView.load(CDN_URL + ingredient.image)
                ingredientNameTextView.text = ingredient.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                ingredientAmountTextView.text = ingredient.amount.toString()
                ingredientUnitTextView.text = ingredient.unit
                ingredientConsistencyTextView.text = ingredient.consistency
                ingredientOriginalTextView.text = ingredient.original
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = IngredientsRowLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}