package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.data.DataStoreRepository
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

    private var mealType = Constants.DEFAULT_MEAL_TYPE
    private var dietType = Constants.DEFAULT_DIET_TYPE

    private val _recipesFlow = repository.localDataSource.loadRecipes()
    val readRecipes =
        _recipesFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    private var backFromBottomSheet = false

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

    private fun getQueries(): HashMap<String, String> {

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

    private fun getSearchQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[Constants.QUERY_SEARCH] = searchQuery
        queries[Constants.QUERY_NUMBER] = Constants.DEFAULT_RECIPES_NUMBER
        queries[Constants.QUERY_API_KEY] = Constants.API_KEY
        queries[Constants.QUERY_RECIPE_INFO] = true.toString()
        queries[Constants.QUERY_FILL_INGREDIENTS] = true.toString()

        return queries
    }

    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipes>> = MutableLiveData()

    private fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()

        if (hasInternetConnection()) {
            try {
                val response = repository.remoteDataSource.searchRecipes(searchQuery)
                recipesResponse.value = handleServerResponse(response)
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

    private fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
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

    fun searchApiData(searchString: String) {
        searchRecipes(getSearchQuery(searchString))
    }

    private fun requestApiData() {
        getRecipes(getQueries())
    }

    private fun getListOfRecipes() {
        viewModelScope.launch {
            readRecipes.value?.let {
                if (it.isNotEmpty() && !backFromBottomSheet) {
                    it.lastOrNull()?.foodRecipes?.let { recipes ->
                        recipesResponse.value = NetworkResult.Success(recipes)
                    }
                } else {
                    requestApiData()
                }
            }
        }
    }

    fun initVm(backFromBottomSheet: Boolean) {
        this.backFromBottomSheet = backFromBottomSheet
        viewModelScope.launch {
            networkListener.checkNetworkAvailability().collect {
                getListOfRecipes()
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