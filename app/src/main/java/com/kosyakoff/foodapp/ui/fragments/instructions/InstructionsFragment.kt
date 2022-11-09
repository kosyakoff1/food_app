package com.kosyakoff.foodapp.ui.fragments.instructions

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.databinding.FragmentInstructionsBinding
import com.kosyakoff.foodapp.viewmodels.DetailsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InstructionsFragment : Fragment(R.layout.fragment_instructions) {

    private val binding: FragmentInstructionsBinding by viewBinding(FragmentInstructionsBinding::bind)
    private val detailsViewModel: DetailsViewModel by activityViewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        with(binding) {
            instructionsWebView.webViewClient = object : WebViewClient() {}
            instructionsWebView.settings.javaScriptEnabled = true

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    detailsViewModel.uiState.collectLatest { state ->
                        state.currentRecipe.sourceUrl?.let { instructionsWebView.loadUrl(it) }
                    }
                }

            }
        }
    }
}