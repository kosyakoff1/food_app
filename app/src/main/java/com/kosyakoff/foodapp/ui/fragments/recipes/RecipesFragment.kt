package com.kosyakoff.foodapp.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.adapters.RecipesAdapter
import com.kosyakoff.foodapp.databinding.FragmentRecipesBinding
import com.kosyakoff.foodapp.models.FoodRecipes
import com.kosyakoff.foodapp.util.NetworkListener
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.util.observeOnce
import com.kosyakoff.foodapp.viewmodels.MainViewModel
import com.kosyakoff.foodapp.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {

    private val recipesFragmentArgs: RecipesFragmentArgs by navArgs()
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

        setHasOptionsMenu(true)

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

    private fun searchApiData(searchString: String) {

        mainViewModel.searchRecipesResponse.removeObservers(viewLifecycleOwner)

        mainViewModel.searchRecipes(recipesViewModel.getSearchQuery(searchString))
        mainViewModel.searchRecipesResponse.observe(viewLifecycleOwner) {
            Log.d("searchRecipesResponse", "searchRecipesResponse")
            processRecipesNetworkResult(it)
        }
    }

    private fun requestApiData() {
        mainViewModel.recipesResponse.removeObservers(viewLifecycleOwner)

        mainViewModel.getRecipes(recipesViewModel.getQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) {
            Log.d("recipesResponse", "recipesResponse")
            processRecipesNetworkResult(it)
        }
    }

    private fun processRecipesNetworkResult(response: NetworkResult<FoodRecipes>) {
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


    private fun toggleShimmerEffect(on: Boolean) {
        if (on) {
            binding.recipesRecyclerView.showShimmer()
        } else {
            binding.recipesRecyclerView.hideShimmer()
        }

    }

}