package com.kosyakoff.foodapp.viewmodels

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kosyakoff.foodapp.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipesViewModel {

    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    private var mealType = Constants.DEFAULT_MEAL_TYPE
    private var dietType = Constants.DEFAULT_DIET_TYPE

    val readMealAndDietType = dataStoreRepository.readMealAndDietType


    fun saveMealAndDietTypes(
        selectedMealType: String,
        selectedMealTypeId: Int,
        selectedDietType: String,
        selectedDietTypeId: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietTypes(
                selectedMealType,
                selectedMealTypeId,
                selectedDietType,
                selectedDietTypeId
            )
        }
    }

    fun getQueries(): HashMap<String, String> {

        viewModelScope.launch {
            readMealAndDietType.collectLatest { value ->
                mealType = value.selectedMealType
                dietType = value.selectedDietType
            }
        }

        val queries: HashMap<String, String> = HashMap()
        queries[Constants.QUERY_NUMBER] = Constants.DEFAULT_RECIPES_NUMBER
        queries[Constants.QUERY_API_KEY] = Constants.API_KEY
        queries[Constants.QUERY_TYPE] = mealType
        queries[Constants.QUERY_DIET] = dietType
        queries[Constants.QUERY_RECIPE_INFO] = true.toString()
        queries[Constants.QUERY_FILL_INGREDIENTS] = true.toString()

        return queries
    }

    fun getSearchQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[Constants.QUERY_SEARCH] = searchQuery
        queries[Constants.QUERY_NUMBER] = Constants.DEFAULT_RECIPES_NUMBER
        queries[Constants.QUERY_API_KEY] = Constants.API_KEY
        queries[Constants.QUERY_RECIPE_INFO] = true.toString()
        queries[Constants.QUERY_FILL_INGREDIENTS] = true.toString()

        return queries
    }

}