package com.kosyakoff.foodapp.ui.fragments.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.adapters.FavoriteRecipesAdapter
import com.kosyakoff.foodapp.databinding.FragmentFavouriteRecipesBinding
import com.kosyakoff.foodapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavouriteRecipesFragment : Fragment(R.layout.fragment_favourite_recipes) {

    private val binding: FragmentFavouriteRecipesBinding by viewBinding(
        FragmentFavouriteRecipesBinding::bind
    )
    private val adapter: FavoriteRecipesAdapter by lazy { FavoriteRecipesAdapter() }
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mainViewModel.readFavoriteRecipes.observe(viewLifecycleOwner) { favoriteEntities ->
            adapter.submitList(favoriteEntities.map { favoriteEntity -> favoriteEntity.recipe })
        }
    }

    private fun setupRecyclerView() {
        with(binding) {
            favoriteRecipesRecyclerView.adapter = adapter
            favoriteRecipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}