package com.kosyakoff.foodapp.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.navigation.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.adapters.RecipePagerAdapter
import com.kosyakoff.foodapp.data.database.entities.FavoriteEntity
import com.kosyakoff.foodapp.databinding.ActivityDetailsBinding
import com.kosyakoff.foodapp.ui.base.BaseActivity
import com.kosyakoff.foodapp.ui.fragments.ingredients.IngredientsFragment
import com.kosyakoff.foodapp.ui.fragments.instructions.InstructionsFragment
import com.kosyakoff.foodapp.ui.fragments.overview.OverviewFragment
import com.kosyakoff.foodapp.util.extensions.showToast
import com.kosyakoff.foodapp.util.observeOnce
import com.kosyakoff.foodapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity(R.layout.activity_details), BaseActivity {
    private val binding: ActivityDetailsBinding by viewBinding(ActivityDetailsBinding::bind)
    private val args: DetailsActivityArgs by navArgs()
    private val mainViewModel: MainViewModel by viewModels()

    private var isFavored = false

    companion object {
        const val BUNDLE_KEY = "recipeBundle"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_activity_menu, menu)

        updateMenuVisuals(menu?.findItem(R.id.save_to_favorites_menu)!!)

        return true
    }

    private fun updateMenuVisuals(menuItem: MenuItem) =
        mainViewModel.readFavoriteRecipes.observeOnce(this) { favoriteEntities ->
            favoriteEntities.firstOrNull { entity -> entity.recipe.id == args.recipe.id }?.let {
                isFavored = true
                setMenuStarIsFavored(menuItem, isFavored)
            }
        }

    private fun setMenuStarIsFavored(menuItem: MenuItem, favored: Boolean) {
        menuItem.icon =
            ContextCompat.getDrawable(
                this,
                if (favored) R.drawable.ic_favorite_selected_star else R.drawable.ic_favorite_unselected_star
            )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.save_to_favorites_menu) {
            if (isFavored) {
                removeRecipeFromFavorites(item)
            } else {
                writeRecipeToFavorites(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun removeRecipeFromFavorites(menuItem: MenuItem) {

        mainViewModel.readFavoriteRecipes.observeOnce(this) { favoriteEntities ->
            favoriteEntities.firstOrNull { entity -> entity.recipe.id == args.recipe.id }?.let {
                isFavored = false
                setMenuStarIsFavored(menuItem, isFavored)
                showToast(getString(R.string.scr_details_tst_recipe_removed_from_favorites))
                mainViewModel.deleteFavoriteRecipe(it)
            }
        }
    }

    private fun writeRecipeToFavorites(menuItem: MenuItem) {
        mainViewModel.writeFavoriteRecipe(FavoriteEntity(0, args.recipe.id, args.recipe))
        isFavored = true
        setMenuStarIsFavored(menuItem, isFavored)
        showToast(getString(R.string.scr_details_tst_recipe_saved_to_favorites))
    }

    override fun setupViews() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        with(binding) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        setupInsets()

        val fragments = listOf(
            OverviewFragment(),
            IngredientsFragment(),
            InstructionsFragment()
        )

        val adapter = RecipePagerAdapter(
            bundleOf(BUNDLE_KEY to args.recipe),
            fragments,
            this
        )

        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.adapter = adapter
        val titles = listOf(
            getString(R.string.str_overview),
            getString(R.string.str_ingredients),
            getString(R.string.str_instructions)
        )

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    override fun setupInsets() {
        binding.appBar.applyInsetter {
            type(statusBars = true) {
                // Add to padding on all sides
                padding()
            }
        }
    }

}