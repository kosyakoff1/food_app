package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.kosyakoff.foodapp.util.Constants.Companion.API_KEY
import com.kosyakoff.foodapp.util.Constants.Companion.QUERY_API_KEY
import com.kosyakoff.foodapp.util.Constants.Companion.QUERY_DIET
import com.kosyakoff.foodapp.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.kosyakoff.foodapp.util.Constants.Companion.QUERY_NUMBER
import com.kosyakoff.foodapp.util.Constants.Companion.QUERY_RECIPE_INFO
import com.kosyakoff.foodapp.util.Constants.Companion.QUERY_TYPE

class RecipesViewModel(application: Application) : AndroidViewModel(application) {

    fun getQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_NUMBER] = "50"
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = "snack"
        queries[QUERY_DIET] = "vegan"
        queries[QUERY_RECIPE_INFO] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }
}