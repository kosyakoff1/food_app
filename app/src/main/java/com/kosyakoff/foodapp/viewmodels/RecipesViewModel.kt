package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.data.DataStoreRepository
import com.kosyakoff.foodapp.data.MealAndDietType
import com.kosyakoff.foodapp.data.Repository
import com.kosyakoff.foodapp.data.database.entities.RecipesEntity
import com.kosyakoff.foodapp.models.FoodRecipes
import com.kosyakoff.foodapp.states.RecipesUIState
import com.kosyakoff.foodapp.states.UserMessage
import com.kosyakoff.foodapp.ui.base.BaseViewModel
import com.kosyakoff.foodapp.util.Constants
import com.kosyakoff.foodapp.util.NetworkListener
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.util.extensions.getString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository,
    private val repository: Repository,
    private val networkListener: NetworkListener
) : BaseViewModel(application) {

    private val _uiState = MutableStateFlow(
        RecipesUIState(
            userMessages = emptyList(),
            mealAndDietType = MealAndDietType(
                Constants.DEFAULT_MEAL_TYPE, 0, Constants.DEFAULT_DIET_TYPE, 0
            ),
            recipes = NetworkResult.Loading(),
            backFromBottomSheet = false
        )
    )

    val uiState: StateFlow<RecipesUIState> = _uiState

    fun init(backFromBottomSheet: Boolean) {
        _uiState.update { state -> state.copy(backFromBottomSheet = backFromBottomSheet) }

        dataStoreRepository.readMealAndDietType.onEach { mealAndDietType ->
            _uiState.update { state ->
                state.copy(mealAndDietType = mealAndDietType)
            }
        }.launchIn(viewModelScope)

        networkListener.checkNetworkAvailability().onEach {
            getListOfRecipes()
        }.launchIn(viewModelScope)
    }

    fun saveMealAndDietTypes(
        selectedMealType: String,
        selectedMealTypeId: Int,
        selectedDietType: String,
        selectedDietTypeId: Int
    ) {
        viewModelScope.launch {
            dataStoreRepository.saveMealAndDietTypes(
                selectedMealType,
                selectedMealTypeId,
                selectedDietType,
                selectedDietTypeId
            )
        }
    }

    private fun formSearchQuery(searchQuery: String? = null): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        with(_uiState.value.mealAndDietType) {
            queries[Constants.QUERY_NUMBER] = Constants.DEFAULT_RECIPES_NUMBER
            queries[Constants.QUERY_API_KEY] = Constants.API_KEY
            queries[Constants.QUERY_TYPE] = selectedMealType
            queries[Constants.QUERY_DIET] = selectedDietType
            searchQuery?.let { queries[Constants.QUERY_SEARCH] = it }
            queries[Constants.QUERY_RECIPE_INFO] = true.toString()
            queries[Constants.QUERY_FILL_INGREDIENTS] = true.toString()
        }

        return queries
    }

    private suspend fun searchRecipesSafeCall(
        searchQuery: Map<String, String>
    ) {
        _uiState.update {
            it.copy(recipes = NetworkResult.Loading())
        }

        if (hasInternetConnection()) {
            try {
                val response = repository.remoteDataSource.searchRecipes(searchQuery)
                val recipesNetworkResult = handleServerResponse(response)
                _uiState.update { state ->
                    state.copy(recipes = recipesNetworkResult)
                }

                recipesNetworkResult.data?.let { putRecipesInCache(it) }

            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(recipes = NetworkResult.Error(getString(R.string.str_error_recipes_not_found)))
                }
            }
        } else {
            _uiState.update { state ->
                state.copy(
                    recipes = NetworkResult.Error(
                        getString(R.string.str_error_no_internet_connection)
                    )
                )
            }
        }
    }

    private suspend fun insertRecipes(recipesEntity: RecipesEntity) =
        withContext(Dispatchers.IO) {
            repository.localDataSource.insertRecipes(recipesEntity)
        }

    fun searchApiData(searchString: String) {
        viewModelScope.launch {
            searchRecipesSafeCall(formSearchQuery(searchString))
        }
    }

    private fun getListOfRecipes() {
        with(_uiState.value) {
            if (recipes.data?.results?.isNotEmpty() == true && !backFromBottomSheet) {
                viewModelScope.launch {
                    searchRecipesSafeCall(formSearchQuery())
                }
            }
        }
    }


    private suspend fun putRecipesInCache(foodRecipes: FoodRecipes) {
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