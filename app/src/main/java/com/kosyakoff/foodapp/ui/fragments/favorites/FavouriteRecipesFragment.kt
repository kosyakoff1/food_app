package com.kosyakoff.foodapp.ui.fragments.favorites

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.adapters.FavoriteRecipesAdapter
import com.kosyakoff.foodapp.databinding.FragmentFavouriteRecipesBinding
import com.kosyakoff.foodapp.util.Constants.Companion.FAVORITES_SELECTION_NAME
import com.kosyakoff.foodapp.util.extensions.showToast
import com.kosyakoff.foodapp.viewmodels.FavoritesViewModel
import com.kosyakoff.foodapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FavouriteRecipesFragment : Fragment(R.layout.fragment_favourite_recipes),
    ActionMode.Callback {

    private val binding: FragmentFavouriteRecipesBinding by viewBinding(
        FragmentFavouriteRecipesBinding::bind
    )
    private val adapter: FavoriteRecipesAdapter by lazy { FavoriteRecipesAdapter() }
    private val favoritesViewModel by viewModels<FavoritesViewModel>()
    private lateinit var favoriteSelectionTracker: SelectionTracker<Long>
    private var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(savedInstanceState)
        initVm()
    }

    private fun initVm() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                favoritesViewModel.readFavoriteRecipes.collectLatest { favoriteEntities ->
                    adapter.submitList(favoriteEntities.map { favoriteEntity -> favoriteEntity.recipe })
                }
                favoritesViewModel.uiState.collectLatest { currentState ->
                    currentState.userMessages.firstOrNull()?.let {
                        favoritesViewModel.messageShown(it.id)
                    }
                }
            }
        }

    }

    private fun initViews(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        setupRecyclerView()
        savedInstanceState?.let {
            favoriteSelectionTracker.onRestoreInstanceState(it)
            setActionModeBasedOnCurrentSelection()
        }
        binding.favoritesViewModel = favoritesViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        favoriteSelectionTracker.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_all_favorites_menu) {
            favoritesViewModel.deleteAllFavoriteRecipes()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorites_main_menu, menu)
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.favorites_contextual_menu, menu)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionMode?.finish()
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = true

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_favorite_menu) {
            favoritesViewModel.deleteGroupOfFavoriteRecipes(
                favoriteSelectionTracker.selection.toList()
            )
            actionMode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        favoriteSelectionTracker.clearSelection()
    }

    private fun setupRecyclerView() {
        with(binding) {
            favoriteRecipesRecyclerView.setHasFixedSize(true)
            favoriteRecipesRecyclerView.adapter = adapter
            favoriteRecipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

            favoriteSelectionTracker = SelectionTracker.Builder<Long>(
                FAVORITES_SELECTION_NAME,
                favoriteRecipesRecyclerView,
                FavoriteRecipesAdapter.FavoritesItemKeyProvider(adapter),
                FavoriteRecipesAdapter.FavoritesItemDetailsLookup(favoriteRecipesRecyclerView),
                StorageStrategy.createLongStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
            ).build()

            adapter.setSelectionTracker(favoriteSelectionTracker)

            favoriteSelectionTracker.addObserver(
                object : SelectionTracker.SelectionObserver<Long>() {
                    override fun onSelectionChanged() {
                        super.onSelectionChanged()
                        setActionModeBasedOnCurrentSelection()
                    }
                })
        }
    }

    private fun setActionModeBasedOnCurrentSelection() {
        if (favoriteSelectionTracker.selection.isEmpty) {
            actionMode?.finish()
        } else {
            if (actionMode == null) {
                actionMode = requireActivity().startActionMode(
                    this@FavouriteRecipesFragment
                )
                actionMode?.apply {
                    val selectionSize = favoriteSelectionTracker.selection.size()
                    title = resources.getQuantityString(
                        R.plurals.scr_favorites_items_selected_menu_title,
                        selectionSize,
                        selectionSize
                    )
                }
            } else {
                actionMode?.apply {
                    val selectionSize = favoriteSelectionTracker.selection.size()
                    title = resources.getQuantityString(
                        R.plurals.scr_favorites_items_selected_menu_title,
                        selectionSize,
                        selectionSize
                    )
                }
            }
        }
    }
}