package com.kosyakoff.foodapp.ui.fragments.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kosyakoff.foodapp.viewmodels.MainViewModel
import com.kosyakoff.foodapp.adapters.RecipesAdapter
import com.kosyakoff.foodapp.databinding.FragmentRecipesBinding
import com.kosyakoff.foodapp.util.Constants
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()
    private val recipesViewModel: RecipesViewModel by viewModels()
    private lateinit var binding: FragmentRecipesBinding
    private val recipesAdapter by lazy { RecipesAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecipesBinding.inflate(inflater, container, false)

        setupRecyclerView()
        requestApiData()

        return binding.root
    }

    private fun setupRecyclerView() {
        with(binding.recipesRecyclerView) {
            this.adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            toggleShimmerEffect(true)
        }
    }

    private fun requestApiData() {
        mainViewModel.getRecipes(recipesViewModel.getQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    toggleShimmerEffect(false)
                    response.data?.let { recipesAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    toggleShimmerEffect(false)
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
                }
                is NetworkResult.Loading -> {
                    toggleShimmerEffect(true)
                }
            }
        }
    }


    private fun toggleShimmerEffect(on: Boolean) {
        if (on) {
            binding.recipesRecyclerView.showShimmer()
        } else {
            binding.recipesRecyclerView.hideShimmer()
        }

    }
}