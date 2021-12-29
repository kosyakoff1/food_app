package com.kosyakoff.foodapp.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.adapters.RecipePagerAdapter
import com.kosyakoff.foodapp.databinding.ActivityDetailsBinding
import com.kosyakoff.foodapp.ui.fragments.ingredients.IngredientsFragment
import com.kosyakoff.foodapp.ui.fragments.instructions.InstructionsFragment
import com.kosyakoff.foodapp.ui.fragments.overview.OverviewFragment

class DetailsActivity : AppCompatActivity(R.layout.activity_details) {
    private val binding: ActivityDetailsBinding by viewBinding(ActivityDetailsBinding::bind)
    private val args: DetailsActivityArgs by navArgs()

    companion object {
        val BUNDLE_KEY = "recipeBundle"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val fragments = listOf(
            OverviewFragment(),
            IngredientsFragment(),
            InstructionsFragment()
        )

        val titles = listOf(
            getString(R.string.str_overview),
            getString(R.string.str_ingredients),
            getString(R.string.str_instructions)
        )

        val adapter = RecipePagerAdapter(
            bundleOf(BUNDLE_KEY to args.recipe),
            fragments,
            titles,
            supportFragmentManager
        )

        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}