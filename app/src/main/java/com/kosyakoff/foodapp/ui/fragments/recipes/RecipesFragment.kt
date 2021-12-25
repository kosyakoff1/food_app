package com.kosyakoff.foodapp.ui.fragments.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.kosyakoff.foodapp.adapters.RecipesAdapter
import com.kosyakoff.foodapp.databinding.FragmentRecipesBinding
import com.kosyakoff.foodapp.util.NetworkListener
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.util.observeOnce
import com.kosyakoff.foodapp.viewmodels.MainViewModel
import com.kosyakoff.foodapp.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment() {

    val recipesFragmentArgs: RecipesFragmentArgs by navArgs()
    private val mainViewModel: MainViewModel by viewModels()
    private val recipesViewModel: RecipesViewModel by viewModels()
    private lateinit var binding: FragmentRecipesBinding
    private val recipesAdapter by lazy { RecipesAdapter() }
    private lateinit var networkListener: NetworkListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = mainViewModel

        setupRecyclerView()

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner) {
            recipesViewModel.backOnline = it
        }

        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext()).collect { status ->
                recipesViewModel.showNetworkStatus(status)

                getRecipes()
            }
        }

        binding.recipesFab.setOnClickListener {
            if (recipesViewModel.networkIsAvailable) {
                val action = RecipesFragmentDirections.actionRecipesFragmentToRecipesBottomSheet()
                findNavController().navigate(action)
            }
        }

        return binding.root
    }

    private fun getRecipes() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { localData ->
                if (localData.isNotEmpty() && !recipesFragmentArgs.backFromBottomSheet) {
                    recipesAdapter.setData(localData.first().foodRecipes)
                    toggleShimmerEffect(false)
                } else {
                    requestApiData()
                }
            }
        }
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