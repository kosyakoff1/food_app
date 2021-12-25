package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.data.DataStoreRepository
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
import com.kosyakoff.foodapp.util.extensions.showToast
import com.kosyakoff.foodapp.util.getString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE

    val readMealAndDietType = dataStoreRepository.readMealAndDietType

    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    var networkIsAvailable = false
    var backOnline = false

    private fun saveBackOnline(backOnline: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }
    }

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
            readMealAndDietType.collect { value ->
                mealType = value.selectedMealType
                dietType = value.selectedDietType
            }
        }

        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_RECIPE_INFO] = true.toString()
        queries[QUERY_FILL_INGREDIENTS] = true.toString()

        return queries
    }

    fun showNetworkStatus(newNetworkStatus: Boolean?) {
        newNetworkStatus?.let { networkIsAvailable = it }

        if (!networkIsAvailable) {
            getApplication<Application>().showToast(getString(R.string.str_error_no_internet_connection))
            saveBackOnline(true)
        } else if (backOnline) {
            getApplication<Application>().showToast(getString(R.string.str_internet_restored))
            saveBackOnline(false)
        }
    }
}