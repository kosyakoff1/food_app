package com.kosyakoff.foodapp.ui.fragments.instructions

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.databinding.FragmentInstructionsBinding
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.util.Constants.Companion.RECIPE_DETAIL_BUNDLE_KEY

class InstructionsFragment : Fragment(R.layout.fragment_instructions) {

    private val binding: FragmentInstructionsBinding by viewBinding(FragmentInstructionsBinding::bind)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe = arguments?.getParcelable<FoodRecipe>(RECIPE_DETAIL_BUNDLE_KEY)!!

        initViews(recipe)
    }

    private fun initViews(recipe: FoodRecipe) {
        with(binding) {
            instructionsWebView.webViewClient = object : WebViewClient() {}
            instructionsWebView.settings.javaScriptEnabled = true
            recipe.sourceUrl?.let { instructionsWebView.loadUrl(it) }
        }
    }
}