package com.kosyakoff.foodapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.databinding.IngredientsRowLayoutBinding
import com.kosyakoff.foodapp.models.ExtendedIngredients
import com.kosyakoff.foodapp.util.Constants.Companion.CDN_URL
import com.kosyakoff.foodapp.util.RecipesIngredientDiffUtilCallback
import java.util.*

class RecipeIngredientsAdapter :
    ListAdapter<ExtendedIngredients, RecipeIngredientsAdapter.ViewHolder>(
        RecipesIngredientDiffUtilCallback
    ) {

    class ViewHolder(private val binding: IngredientsRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ingredient: ExtendedIngredients) {
            binding.apply {
                ingredientImageView.load(CDN_URL + ingredient.image) {
                    crossfade(600)
                    error(R.drawable.ic_error_placeholder)
                }
                ingredientNameTextView.text = ingredient.name?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                ingredientAmountTextView.text = ingredient.amount.toString()
                ingredientUnitTextView.text = ingredient.unit
                ingredientConsistencyTextView.text = ingredient.consitency
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