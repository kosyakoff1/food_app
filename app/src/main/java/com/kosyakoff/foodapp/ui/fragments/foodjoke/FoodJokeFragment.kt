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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.databinding.FragmentFoodJokeBinding
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.viewmodels.FoodJokeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FoodJokeFragment : Fragment(R.layout.fragment_food_joke) {

    private val foodJokeViewModel: FoodJokeViewModel by viewModels()
    private val binding: FragmentFoodJokeBinding by viewBinding(FragmentFoodJokeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        foodJokeViewModel.initVm()
        initViews()
    }

    private fun initViews() {

        with(binding) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    foodJokeViewModel.foodJokeState.collectLatest { response ->
                        foodJokeErrorTextView.isVisible = response is NetworkResult.Error
                        foodJokeErrorImageView.isVisible = response is NetworkResult.Error
                        foodJokeLoadingProgressBar.isVisible = response is NetworkResult.Loading
                        foodJokeCardLayout.isVisible = response is NetworkResult.Success
                        foodJokeTextView.isVisible = response is NetworkResult.Success

                        when (response) {
                            is NetworkResult.Success -> {
                                foodJokeTextView.text = response.data?.text
                            }
                            is NetworkResult.Error -> {
                                foodJokeErrorTextView.text = response.message.toString()
                            }
                            is NetworkResult.Loading -> {
                            }
                        }
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
                foodJokeViewModel.foodJokeState.value.data?.text ?: getString(R.string.str_no_joke)

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