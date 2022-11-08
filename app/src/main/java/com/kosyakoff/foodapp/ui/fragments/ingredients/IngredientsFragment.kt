package com.kosyakoff.foodapp.ui.fragments.ingredients

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.adapters.RecipeIngredientsAdapter
import com.kosyakoff.foodapp.databinding.FragmentIngredientsBinding
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.util.Constants.Companion.RECIPE_DETAIL_BUNDLE_KEY
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.viewmodels.IngredientsViewModel
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class IngredientsFragment : Fragment(R.layout.fragment_ingredients) {

    private val ingredientsViewModel: IngredientsViewModel by viewModels()
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

        val recipe = arguments?.getParcelable<FoodRecipe>(RECIPE_DETAIL_BUNDLE_KEY)!!
        ingredientsViewModel.initVm(recipe)
        initViews()
    }

    private fun initViews() {
        binding.ingredientsRecyclerView.setHasFixedSize(true)
        binding.ingredientsRecyclerView.adapter = adapter
        binding.ingredientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        with(binding) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    ingredientsViewModel.ingredientsState.collectLatest { response ->
                        ingredientsLoadingProgressBar.isVisible = response is NetworkResult.Loading

                        when (response) {
                            is NetworkResult.Error -> {}
                            is NetworkResult.Loading -> {}
                            is NetworkResult.Success -> {
                                adapter.submitList(
                                    response.data?.extendedIngredients ?: emptyList()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}