package com.kosyakoff.foodapp.ui.fragments.overview

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.databinding.FragmentOverviewBinding
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.viewmodels.DetailsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jsoup.Jsoup


class OverviewFragment : Fragment(R.layout.fragment_overview) {

    private val detailsViewModel: DetailsViewModel by activityViewModels()
    private val binding: FragmentOverviewBinding by viewBinding(FragmentOverviewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailsViewModel.uiState.collectLatest { uiState ->
                    with(binding) {
                        mainImageView.load(uiState.currentRecipe.image)
                        titleTextView.text = uiState.currentRecipe.title
                        numberOfLikesTextView.text = uiState.currentRecipe.aggregateLikes.toString()
                        timeTextView.text = uiState.currentRecipe.readyInMinutes.toString()
                        val parsedString =
                            uiState.currentRecipe.summary?.let { Jsoup.parse(it).text() }
                        overviewTextView.text = parsedString
                    }
                    setCheckedItems(uiState.currentRecipe)
                }
            }
        }
    }

    private fun setCheckedItems(recipe: FoodRecipe) {
        val checkedColor = ContextCompat.getColor(
            requireContext(),
            R.color.green
        )

        with(binding) {
            if (recipe.vegetarian) {
                vegetarianImageView.setColorFilter(checkedColor)
            }
            if (recipe.vegan) {
                veganImageView.setColorFilter(checkedColor)
            }
            if (recipe.glutenFree) {
                glutenFreeImageView.setColorFilter(checkedColor)
            }
            if (recipe.dairyFree) {
                dairyFreeImageView.setColorFilter(checkedColor)
            }
            if (recipe.veryHealthy) {
                healthyImageView.setColorFilter(checkedColor)
            }
            if (recipe.cheap) {
                cheapImageView.setColorFilter(checkedColor)
            }
        }
    }
}