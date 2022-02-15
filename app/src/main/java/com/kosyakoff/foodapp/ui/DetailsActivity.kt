package com.kosyakoff.foodapp.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.adapters.RecipePagerAdapter
import com.kosyakoff.foodapp.databinding.ActivityDetailsBinding
import com.kosyakoff.foodapp.ui.base.BaseActivity
import com.kosyakoff.foodapp.ui.fragments.ingredients.IngredientsFragment
import com.kosyakoff.foodapp.ui.fragments.instructions.InstructionsFragment
import com.kosyakoff.foodapp.ui.fragments.overview.OverviewFragment
import com.kosyakoff.foodapp.util.extensions.showToast
import com.kosyakoff.foodapp.viewmodels.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity(R.layout.activity_details), BaseActivity {
    private val binding: ActivityDetailsBinding by viewBinding(ActivityDetailsBinding::bind)
    private val args: DetailsActivityArgs by navArgs()
    private val detailsViewModel: DetailsViewModel by viewModels()

    private var activityMenu: Menu? = null

    companion object {
        const val BUNDLE_KEY = "recipeBundle"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        setupViewModel()
    }

    override fun setupInsets() {
        binding.appBar.applyInsetter {
            type(statusBars = true) {
                // Add to padding on all sides
                padding()
            }
        }
    }

    override fun setupViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailsViewModel.uiState.collect { uiState ->
                    activityMenu?.findItem(R.id.save_to_favorites_menu)?.let {
                        setMenuStarIsFavored(it, uiState.isFavored)
                    }

                    uiState.userMessages.firstOrNull()?.let { userMessage ->
                        showToast(userMessage.text)
                        detailsViewModel.messageShown(userMessage.id)
                    }
                }
            }
            detailsViewModel.initVm(args.recipe)
        }
    }

    override fun setupViews() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        with(binding) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setupInsets()

            val fragments = listOf(
                OverviewFragment(),
                IngredientsFragment(),
                InstructionsFragment()
            )

            val adapter = RecipePagerAdapter(
                bundleOf(BUNDLE_KEY to args.recipe),
                fragments,
                this@DetailsActivity
            )

            viewPager.isUserInputEnabled = false
            viewPager.adapter = adapter
            val titles = listOf(
                getString(R.string.str_overview),
                getString(R.string.str_ingredients),
                getString(R.string.str_instructions)
            )

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_activity_menu, menu)
        activityMenu = menu
//        activityMenu?.findItem(R.id.save_to_favorites_menu)?.let {
//            setMenuStarIsFavored(it, detailsViewModel. isFavored)
//        }
        return true
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
            detailsViewModel.toggleIsFavored()
        }
        return super.onOptionsItemSelected(item)
    }
}