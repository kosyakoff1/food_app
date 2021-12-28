package com.kosyakoff.foodapp.ui.fragments.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import coil.load
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.databinding.FragmentOverviewBinding
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.ui.DetailsActivity
import org.jsoup.Jsoup


class OverviewFragment : Fragment() {

    private lateinit var binding: FragmentOverviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val recipe = requireArguments().getParcelable<FoodRecipe>(DetailsActivity.BUNDLE_KEY)!!

        binding = FragmentOverviewBinding.inflate(inflater, container, false)
        with(binding) {
            mainImageView.load(recipe.image)
            titleTextView.text = recipe.title
            numberOfLikesTextView.text = recipe.aggregateLikes.toString()
            timeTextView.text = recipe.readyInMinutes.toString()
            val parsedString = Jsoup.parse(recipe.summary).text()
            overviewTextView.text = parsedString
        }
        setCheckedItems(recipe)

        return binding.root
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