package com.kosyakoff.foodapp.ui.fragments.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.databinding.FragmentIngredientsBinding

class IngredientsFragment : Fragment() {

    private val binding: FragmentIngredientsBinding by viewBinding(FragmentIngredientsBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_ingredients, container, false)
    }

}