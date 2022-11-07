package com.kosyakoff.foodapp.ui.fragments.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.kosyakoff.foodapp.databinding.FragmentRecipesBottomSheetBinding
import com.kosyakoff.foodapp.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.kosyakoff.foodapp.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.kosyakoff.foodapp.util.extensions.showToast
import com.kosyakoff.foodapp.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class RecipesBottomSheet : BottomSheetDialogFragment() {

    private var mealTypeChipTitle = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChipTitle = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    private val recipesViewModel: RecipesViewModel by viewModels()

    private var _binding: FragmentRecipesBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecipesBottomSheetBinding.inflate(inflater, container, false)
        initViews()

        initVm()
        return binding.root
    }

    private fun initVm() {


        recipesViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner) { value ->
            mealTypeChipTitle = value.selectedMealType
            dietTypeChipTitle = value.selectedDietType

            binding.apply {
                try {
                    value.selectedMealTypeId.takeIf { it != 0 }?.let {
                        mealTypeChipGroup.findViewById<Chip>(it).apply {
                            isChecked = true
                            mealTypeChipGroup.requestChildFocus(this, this)
                        }
                    }
                    value.selectedDietTypeId.takeIf { it != 0 }?.let {
                        dietTypeChipGroup.findViewById<Chip>(it).apply {
                            isChecked = true
                            dietTypeChipGroup.requestChildFocus(this, this)
                        }
                    }
                } catch (ex: Exception) {
                    context?.showToast(ex.message.toString())
                }
            }

        }
    }

    private fun initViews() {

        binding.mealTypeChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            val selectedMealType = chip.text.toString().lowercase(Locale.ROOT)

            mealTypeChipTitle = selectedMealType
            mealTypeChipId = checkedId
        }

        binding.dietTypeChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            val selectedDietType = chip.text.toString().lowercase(Locale.ROOT)

            dietTypeChipTitle = selectedDietType
            dietTypeChipId = checkedId
        }

        binding.applyButton.setOnClickListener {
            recipesViewModel.saveMealAndDietTypes(
                mealTypeChipTitle,
                mealTypeChipId, dietTypeChipTitle, dietTypeChipId
            )

            val action = RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(
                backFromBottomSheet = true
            )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}