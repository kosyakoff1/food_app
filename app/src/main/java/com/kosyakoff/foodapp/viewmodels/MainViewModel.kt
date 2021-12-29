package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.data.Repository
import com.kosyakoff.foodapp.data.database.entities.RecipesEntity
import com.kosyakoff.foodapp.models.FoodRecipes
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.util.getString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    val readRecipes: LiveData<List<RecipesEntity>> =
        repository.localDataSource.loadRecipes().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.localDataSource.insertRecipes(recipesEntity)
        }

    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipes>> = MutableLiveData()
    var searchRecipesResponse: MutableLiveData<NetworkResult<FoodRecipes>> = MutableLiveData()

    fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchRecipesResponse.value = NetworkResult.Loading()

        if (hasInternetConnection()) {
            try {
                val response = repository.remoteDataSource.searchRecipes(searchQuery)
                searchRecipesResponse.value = handleFoodRecipesResponse(response)
            } catch (e: Exception) {
                searchRecipesResponse.value =
                    NetworkResult.Error(getString(R.string.str_error_recipes_not_found))
            }
        } else {
            searchRecipesResponse.value =
                NetworkResult.Error(
                    getString(R.string.str_error_no_internet_connection)
                )
        }
    }

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()

        if (hasInternetConnection()) {
            try {
                val response = repository.remoteDataSource.getRecipes(queries)
                recipesResponse.value = handleFoodRecipesResponse(response)

                recipesResponse.value!!.data?.let {
                    putRecipesInCache(it)
                }
            } catch (e: Exception) {
                recipesResponse.value =
                    NetworkResult.Error(getString(R.string.str_error_recipes_not_found))
            }
        } else {
            recipesResponse.value =
                NetworkResult.Error(
                    getString(R.string.str_error_no_internet_connection)
                )
        }
    }

    private fun putRecipesInCache(foodRecipes: FoodRecipes) {
        insertRecipes(RecipesEntity(0, foodRecipes))
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipes>): NetworkResult<FoodRecipes> {
        when {
            response.message().contains("timeout") -> {
                return NetworkResult.Error(getString(R.string.str_error_timeout))
            }
            response.code() == 402 -> {
                return NetworkResult.Error(getString(R.string.str_error_api_limit_reached))
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error(getString(R.string.str_error_recipes_not_found))
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()!!
                return NetworkResult.Success(foodRecipes)
            }
            else -> {
                return NetworkResult.Error(response.message().toString())
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<Application>()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false

        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}