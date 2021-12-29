package com.kosyakoff.foodapp.ui.fragments.instructions

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.databinding.FragmentInstructionsBinding
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.ui.DetailsActivity

class InstructionsFragment : Fragment(R.layout.fragment_instructions) {

    private val binding: FragmentInstructionsBinding by viewBinding(FragmentInstructionsBinding::bind)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe = arguments?.getParcelable<FoodRecipe>(DetailsActivity.BUNDLE_KEY)!!

        with(binding) {
            instructionsWebView.webViewClient = object : WebViewClient() {}
            instructionsWebView.settings.javaScriptEnabled = true
            instructionsWebView.loadUrl(recipe.sourceUrl)
        }
    }
}