package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.data.DataStoreRepository
import com.kosyakoff.foodapp.data.Repository
import com.kosyakoff.foodapp.data.database.entities.RecipesEntity
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.models.FoodRecipes
import com.kosyakoff.foodapp.states.DetailsUIState
import com.kosyakoff.foodapp.states.RecipesUIState
import com.kosyakoff.foodapp.states.UserMessage
import com.kosyakoff.foodapp.ui.base.BaseViewModel
import com.kosyakoff.foodapp.util.Constants
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.util.extensions.getString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository,
    private val repository: Repository
) : BaseViewModel(application) {

    private var mealType = Constants.DEFAULT_MEAL_TYPE
    private var dietType = Constants.DEFAULT_DIET_TYPE


    private val _recipesFlow = repository.localDataSource.loadRecipes()
    private var _readRecipes: MutableLiveData<List<RecipesEntity>> = MutableLiveData(null)
    val readRecipes: LiveData<List<RecipesEntity>> get() = _readRecipes

    val readMealAndDietType = dataStoreRepository.readMealAndDietType

    private val _uiState =
        MutableStateFlow(
            RecipesUIState(
                userMessages = emptyList()
            )
        )
    val uiState: StateFlow<RecipesUIState> = _uiState


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
                searchRecipesResponse.value = handleServerResponse(response)
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

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.localDataSource.insertRecipes(recipesEntity)
        }

    private suspend fun fetchRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()

        if (hasInternetConnection()) {
            try {
                val response = repository.remoteDataSource.getRecipes(queries)
                recipesResponse.value = handleServerResponse(response)

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

    private fun putRecipesInCache(foodRecipes: FoodRecipes) {
        insertRecipes(RecipesEntity(0, foodRecipes))
    }

    override fun messageShown(messageId: Long) {
        _uiState.update { currentUiState ->
            val messages = currentUiState.userMessages.filterNot { it.id == messageId }
            currentUiState.copy(userMessages = messages)
        }
    }

    override fun addMessageToQueue(message: String) {
        _uiState.update { currentState ->
            val messages = currentState.userMessages + UserMessage(
                id = UUID.randomUUID().mostSignificantBits,
                text = message
            )
            currentState.copy(userMessages = messages)
        }
    }

}