package com.kosyakoff.foodapp.ui.fragments.ingredients

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.adapters.RecipeIngredientsAdapter
import com.kosyakoff.foodapp.databinding.FragmentIngredientsBinding
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.ui.DetailsActivity
import dev.chrisbanes.insetter.applyInsetter

class IngredientsFragment : Fragment(R.layout.fragment_ingredients) {

    private val binding: FragmentIngredientsBinding by viewBinding(FragmentIngredientsBinding::bind)
    private val adapter: RecipeIngredientsAdapter by lazy { RecipeIngredientsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ingredientsRecyclerView.applyInsetter {
            type(navigationBars = true) {
                // Add to padding on all sides
                padding()
            }
        }

        val recipe = arguments?.getParcelable<FoodRecipe>(DetailsActivity.BUNDLE_KEY)!!

        initViews()
        adapter.submitList(recipe.extendedIngredients)
    }

    private fun initViews() {
        binding.ingredientsRecyclerView.setHasFixedSize(true)
        binding.ingredientsRecyclerView.adapter = adapter
        binding.ingredientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

}