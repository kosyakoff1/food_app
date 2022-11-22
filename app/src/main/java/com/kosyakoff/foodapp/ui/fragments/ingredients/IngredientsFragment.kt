package com.kosyakoff.foodapp.ui.fragments.ingredients

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.adapters.RecipeIngredientsAdapter
import com.kosyakoff.foodapp.databinding.FragmentIngredientsBinding
import com.kosyakoff.foodapp.viewmodels.DetailsViewModel
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class IngredientsFragment : Fragment(R.layout.fragment_ingredients) {

    private val viewModel: DetailsViewModel by activityViewModels()
    private val binding: FragmentIngredientsBinding by viewBinding(FragmentIngredientsBinding::bind)
    private val adapter: RecipeIngredientsAdapter by lazy { RecipeIngredientsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        bindVm()
    }

    private fun bindVm() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    adapter.submitList(
                        it.currentRecipe.extendedIngredients
                    )
                }
            }
        }
    }

    private fun initViews() {

        with(binding.ingredientsRecyclerView) {
            applyInsetter {
                type(navigationBars = true) {
                    // Add to padding on all sides
                    padding()
                }
            }
            setHasFixedSize(true)
            adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}