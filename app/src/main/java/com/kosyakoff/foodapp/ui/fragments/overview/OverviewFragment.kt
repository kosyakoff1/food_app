package com.kosyakoff.foodapp.ui.fragments.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.databinding.FragmentOverviewBinding
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.ui.DetailsActivity
import org.jsoup.Jsoup


class OverviewFragment : Fragment(R.layout.fragment_overview) {

    private val binding: FragmentOverviewBinding by viewBinding(FragmentOverviewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe = requireArguments().getParcelable<FoodRecipe>(DetailsActivity.BUNDLE_KEY)!!

        with(binding) {
            mainImageView.load(recipe.image)
            titleTextView.text = recipe.title
            numberOfLikesTextView.text = recipe.aggregateLikes.toString()
            timeTextView.text = recipe.readyInMinutes.toString()
            val parsedString = Jsoup.parse(recipe.summary).text()
            overviewTextView.text = parsedString
        }
        setCheckedItems(recipe)
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