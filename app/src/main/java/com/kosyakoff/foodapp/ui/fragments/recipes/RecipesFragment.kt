package com.kosyakoff.foodapp.ui.fragments.recipes

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.kosyakoff.foodapp.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecipesFragment : Fragment(R.layout.fragment_recipes), SearchView.OnQueryTextListener {

    private val recipesFragmentArgs: RecipesFragmentArgs by navArgs()
    private val recipesViewModel: RecipesViewModel by viewModels()
    private val binding by viewBinding(FragmentRecipesBinding::bind)
    private val recipesAdapter by lazy { RecipesAdapter() }

    @Inject
    lateinit var networkListener: NetworkListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        recipesViewModel.initVm(recipesFragmentArgs.backFromBottomSheet)

    }

    private fun initViews() {
        binding.viewModel = recipesViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setHasOptionsMenu(true)

        setupRecyclerView()

        binding.recipesFab.setOnClickListener {
            if (networkListener.checkNetworkAvailability().value) {
                val action = RecipesFragmentDirections.actionRecipesFragmentToRecipesBottomSheet()
                findNavController().navigate(action)
            }
        }

        recipesViewModel.recipesResponse.observe(viewLifecycleOwner) {
            processRecipesNetworkResult(it)
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
            recipesViewModel.searchApiData(it)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }


    private fun setupRecyclerView() {
        with(binding.recipesRecyclerView) {
            setHasFixedSize(true)
            this.adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            toggleShimmerEffect(true)
        }
    }


    private fun processRecipesNetworkResult(response: NetworkResult<FoodRecipes>) {
        binding.errorImageView.isVisible = response is NetworkResult.Error
        binding.errorTextView.isVisible = response is NetworkResult.Error
        toggleShimmerEffect(response is NetworkResult.Loading)
        when (response) {
            is NetworkResult.Success -> {
                response.data?.let { recipesAdapter.submitList(it.results) }
            }
            is NetworkResult.Error -> {
                binding.errorTextView.text = response.message.toString()
            }
            is NetworkResult.Loading -> {
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