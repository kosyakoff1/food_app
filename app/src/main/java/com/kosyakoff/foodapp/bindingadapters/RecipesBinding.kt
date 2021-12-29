package com.kosyakoff.foodapp.bindingadapters

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.kosyakoff.foodapp.data.database.entities.RecipesEntity
import com.kosyakoff.foodapp.models.FoodRecipes
import com.kosyakoff.foodapp.util.NetworkResult

object RecipesBinding {

    @JvmStatic
    @BindingAdapter("readApiResponse", "readDatabase", requireAll = true)
    fun errorViewVisibility(
        view: View,
        apiResponse: NetworkResult<FoodRecipes>?,
        databaseData: List<RecipesEntity>?
    ) {
        if (apiResponse is NetworkResult.Error && databaseData.isNullOrEmpty()) {
            (view as? TextView)?.let { view.text = apiResponse.message }
            view.isVisible = true
        } else {
            view.isVisible = false
        }
    }
}