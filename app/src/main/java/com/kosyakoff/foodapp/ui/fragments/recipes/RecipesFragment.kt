package com.kosyakoff.foodapp.ui.fragments.recipes

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.adapters.RecipesAdapter
import com.kosyakoff.foodapp.databinding.FragmentRecipesBinding
import com.kosyakoff.foodapp.models.FoodRecipes
import com.kosyakoff.foodapp.util.NetworkListener
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.util.observeOnce
import com.kosyakoff.foodapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(R.layout.fragment_recipes), SearchView.OnQueryTextListener {

    private val recipesFragmentArgs: RecipesFragmentArgs by navArgs()
    private val mainViewModel: MainViewModel by viewModels()
    private val binding by viewBinding(FragmentRecipesBinding::bind)
    private val recipesAdapter by lazy { RecipesAdapter() }
    private lateinit var networkListener: NetworkListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setHasOptionsMenu(true)

        setupRecyclerView()

        mainViewModel.readBackOnline.observe(viewLifecycleOwner) {
            mainViewModel.backOnline = it
        }

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext()).collect { status ->
                recipesViewModel.showNetworkStatus(status)
                startGettingRecipes()
            }
        }

        mainViewModel.getListOfRecipes()

        binding.recipesFab.setOnClickListener {
            if (mainViewModel.networkIsAvailable) {
                val action = RecipesFragmentDirections.actionRecipesFragmentToRecipesBottomSheet()
                findNavController().navigate(action)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_fragment_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.apply {
            isSubmitButtonEnabled = true
            setOnQueryTextListener(this@RecipesFragment)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            searchApiData(it)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun startGettingRecipes() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { localData ->
                    if (localData.isNotEmpty() && !recipesFragmentArgs.backFromBottomSheet) {
                        recipesAdapter.submitList(localData.first().foodRecipes.results)
                        toggleShimmerEffect(false)
                    } else {
                        requestApiData()
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        with(binding.recipesRecyclerView) {
            setHasFixedSize(true)
            this.adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            toggleShimmerEffect(true)
        }
    }

    private fun searchApiData(searchString: String) {

        mainViewModel.searchRecipesResponse.removeObservers(viewLifecycleOwner)

        mainViewModel.searchRecipes(mainViewModel.getSearchQuery(searchString))
        mainViewModel.searchRecipesResponse.observe(viewLifecycleOwner) {
            processRecipesNetworkResult(it)
        }
    }

    private fun requestApiData() {
        mainViewModel.recipesResponse.removeObservers(viewLifecycleOwner)

        mainViewModel.getRecipes(mainViewModel.getQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) {
            processRecipesNetworkResult(it)
        }
    }

    private fun processRecipesNetworkResult(response: NetworkResult<FoodRecipes>) {
        binding.errorImageView.isVisible = false
        binding.errorTextView.isVisible = false
        when (response) {
            is NetworkResult.Success -> {
                toggleShimmerEffect(false)
                response.data?.let { recipesAdapter.submitList(it.results) }
            }
            is NetworkResult.Error -> {
                toggleShimmerEffect(false)
                binding.errorImageView.isVisible = true
                binding.errorTextView.isVisible = true
                binding.errorTextView.text = response.message.toString()
            }
            is NetworkResult.Loading -> {
                toggleShimmerEffect(true)
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