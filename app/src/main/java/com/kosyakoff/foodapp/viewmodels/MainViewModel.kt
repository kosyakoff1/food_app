package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.kosyakoff.FoodApplication
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.data.DataStoreRepository
import com.kosyakoff.foodapp.data.Repository
import com.kosyakoff.foodapp.data.database.entities.FavoriteEntity
import com.kosyakoff.foodapp.data.database.entities.RecipesEntity
import com.kosyakoff.foodapp.models.FoodJoke
import com.kosyakoff.foodapp.models.FoodRecipes
import com.kosyakoff.foodapp.ui.base.BaseViewModel
import com.kosyakoff.foodapp.util.Constants
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.util.extensions.appContext
import com.kosyakoff.foodapp.util.extensions.showToast
import com.kosyakoff.foodapp.util.extensions.getString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : BaseViewModel(application) {

    private val _recipesFlow = repository.localDataSource.loadRecipes()
    private var _readRecipes: MutableLiveData<List<RecipesEntity>> = MutableLiveData(null)
    val readRecipes: LiveData<List<RecipesEntity>> get() = _readRecipes
    //_recipesFlow.asLiveData()


    private val _readFavoriteRecipes =
        repository.localDataSource.loadFavoriteRecipes()
    val readFavoriteRecipes: LiveData<List<FavoriteEntity>> =
        _readFavoriteRecipes.asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.localDataSource.insertRecipes(recipesEntity)
        }


    var networkIsAvailable = false
    var backOnline = false

    private fun saveBackOnline(backOnline: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }
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

    fun deleteAllFavoriteRecipes() = viewModelScope.launch(Dispatchers.IO) {
        repository.localDataSource.deleteAllFavoriteRecipes()
    }

    fun deleteGroupOfFavoriteRecipes(group: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.localDataSource.deleteGroupOfFavoriteRecipes(group)

            withContext(Dispatchers.Main) {
                getApplication<FoodApplication>().showToast(getString(R.string.scr_details_tst_recipes_removed_from_favorites))
            }
        }
    }

    var foodJokeResponse: MutableLiveData<NetworkResult<FoodJoke>> = MutableLiveData()
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
        fetchRecipesSafeCall(queries)
    }

    fun getFoodJoke(apiKey: String) = viewModelScope.launch {
        fetchFoodJokeSafeCall(apiKey)
    }

    private suspend fun fetchFoodJokeSafeCall(apiKey: String) {
        foodJokeResponse.value = NetworkResult.Loading()

        if (hasInternetConnection()) {
            try {
                val response = repository.remoteDataSource.getFoodJoke(apiKey)
                foodJokeResponse.value = handleFoodRecipesResponse(response)

            } catch (e: Exception) {
                foodJokeResponse.value =
                    NetworkResult.Error(e.message)
            }
        } else {
            foodJokeResponse.value =
                NetworkResult.Error(
                    getString(R.string.str_error_no_internet_connection)
                )
        }
    }

    private suspend fun fetchRecipesSafeCall(queries: Map<String, String>) {
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

    private fun <T> handleFoodRecipesResponse(response: Response<T>): NetworkResult<T> {
        when {
            response.message().contains("timeout") -> {
                return NetworkResult.Error(getString(R.string.str_error_timeout))
            }
            response.code() == 402 -> {
                return NetworkResult.Error(getString(R.string.str_error_api_limit_reached))
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
            appContext
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

    fun getListOfRecipes() {
        viewModelScope.launch {
            _recipesFlow.lastOrNull()?.let {
                if (it.isNotEmpty() /*&& !recipesFragmentArgs.backFromBottomSheet*/) {
                    _readRecipes.value = it
                } else {
                    requestApiData()
                }
            }
        }
        _recipesFlow.lastOrNull() { localData ->
            if (localData.isNotEmpty() && !recipesFragmentArgs.backFromBottomSheet) {
                recipesAdapter.submitList(localData.first().foodRecipes.results)
                toggleShimmerEffect(false)
            } else {
                requestApiData()
            }
        }
    }
}