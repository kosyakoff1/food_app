package com.kosyakoff.foodapp.ui.fragments.foodjoke

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.databinding.FragmentFoodJokeBinding
import com.kosyakoff.foodapp.util.Constants.Companion.API_KEY
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodJokeFragment : Fragment(R.layout.fragment_food_joke) {

    private val mainViewModel: MainViewModel by viewModels()
    private val binding: FragmentFoodJokeBinding by viewBinding(FragmentFoodJokeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        mainViewModel.getFoodJoke(API_KEY)
        with(binding) {
            mainViewModel.foodJokeResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        foodJokeCardLayout.isVisible = true
                        foodJokeTextView.text = response.data?.text

                        foodJokeErrorTextView.isVisible = false
                        foodJokeErrorImageView.isVisible = false
                        foodJokeLoadingProgressBar.isVisible = false
                    }
                    is NetworkResult.Error -> {
                        foodJokeErrorTextView.isVisible = true
                        foodJokeErrorImageView.isVisible = true
                        foodJokeErrorTextView.text = response.message.toString()

                        foodJokeTextView.isVisible = false
                        foodJokeLoadingProgressBar.isVisible = false
                    }
                    is NetworkResult.Loading -> {
                        foodJokeLoadingProgressBar.isVisible = true
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.food_joke_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share_food_joke_menu) {
            val message =
                mainViewModel.foodJokeResponse.value?.message ?: getString(R.string.str_no_joke)

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }
            startActivity(shareIntent)
        }
        return true
    }

}