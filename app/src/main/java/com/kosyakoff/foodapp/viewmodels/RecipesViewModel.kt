package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.kosyakoff.foodapp.util.Constants
import com.kosyakoff.foodapp.util.Constants.Companion.API_KEY
import com.kosyakoff.foodapp.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.kosyakoff.foodapp.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.kosyakoff.foodapp.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.kosyakoff.foodapp.util.Constants.Companion.QUERY_API_KEY
import com.kosyakoff.foodapp.util.Constants.Companion.QUERY_DIET
import com.kosyakoff.foodapp.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.kosyakoff.foodapp.util.Constants.Companion.QUERY_NUMBER
import com.kosyakoff.foodapp.util.Constants.Companion.QUERY_RECIPE_INFO
import com.kosyakoff.foodapp.util.Constants.Companion.QUERY_TYPE

class RecipesViewModel(application: Application) : AndroidViewModel(application) {

    fun getQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = DEFAULT_MEAL_TYPE
        queries[QUERY_DIET] = DEFAULT_DIET_TYPE
        queries[QUERY_RECIPE_INFO] = true.toString()
        queries[QUERY_FILL_INGREDIENTS] = true.toString()

        return queries
    }
}