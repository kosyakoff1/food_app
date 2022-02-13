package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.data.Repository
import com.kosyakoff.foodapp.data.database.entities.FavoriteEntity
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.states.DetailsUIState
import com.kosyakoff.foodapp.states.UserMessage
import com.kosyakoff.foodapp.ui.base.BaseViewModel
import com.kosyakoff.foodapp.util.extensions.getString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : BaseViewModel(application) {

    private val _uiState =
        MutableStateFlow(
            DetailsUIState(
                currentRecipe = FoodRecipe(),
                isFavored = false,
                userMessages = emptyList()
            )
        )
    val uiState: StateFlow<DetailsUIState> = _uiState

    init {
        viewModelScope.launch {
            repository.localDataSource.loadFavoriteRecipes()
                .collectLatest { listOfRecipes ->
                    _uiState.update { currentUiState ->
                        currentUiState.copy(
                            isFavored = isRecipeIsFavored(
                                currentUiState.currentRecipe.id, listOfRecipes
                            )
                        )

                    }
                }
        }
    }

    fun initVm(recipe: FoodRecipe) {
        viewModelScope.launch {
            val isFavored = isRecipeIsFavored(recipe.id)

            _uiState.update { currentUiState ->
                currentUiState.copy(currentRecipe = recipe, isFavored = isFavored)
            }
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


    private suspend fun getFavoredRecipe(
        recipeId: Int,
        listOfFavorites: List<FavoriteEntity>? = null
    ): FavoriteEntity? {

        val recipe =
            if (listOfFavorites != null) {
                listOfFavorites.firstOrNull { entity -> entity.recipe.id == recipeId }
            } else {
                repository.localDataSource.loadFavoriteRecipes().lastOrNull()
                    ?.firstOrNull { entity -> entity.recipe.id == recipeId }
            }

        return recipe
    }

    override fun userMessageShown(messageId: Long) {
        _uiState.update { currentUiState ->
            val messages = currentUiState.userMessages.filterNot { it.id == messageId }
            currentUiState.copy(userMessages = messages)
        }
    }

    private suspend fun isRecipeIsFavored(
        recipeId: Int,
        listOfFavorites: List<FavoriteEntity>? = null
    ): Boolean {
        var isFavored = false
        getFavoredRecipe(recipeId, listOfFavorites)?.let { isFavored = true }

        return isFavored
    }

    fun toggleIsFavored() {
        if (!_uiState.value.isFavored) {
            writeFavoriteRecipe(
                FavoriteEntity(
                    0,
                    _uiState.value.currentRecipe.id,
                    _uiState.value.currentRecipe
                )
            )
            addMessageToQueue(
                getString(R.string.scr_details_tst_recipe_saved_to_favorites)
            )
        } else {
            viewModelScope.launch {
                getFavoredRecipe(_uiState.value.currentRecipe.id)?.let {
                    deleteFavoriteRecipe(it)

                    addMessageToQueue(
                        getString(R.string.scr_details_tst_recipe_removed_from_favorites)
                    )
                }
            }
        }
    }

    private fun writeFavoriteRecipe(recipe: FavoriteEntity) =
        viewModelScope.launch {
            repository.localDataSource.insertFavoriteRecipe(recipe)
        }

    private fun deleteFavoriteRecipe(recipe: FavoriteEntity) =
        viewModelScope.launch {
            repository.localDataSource.deleteFavoriteRecipe(recipe)
        }
}