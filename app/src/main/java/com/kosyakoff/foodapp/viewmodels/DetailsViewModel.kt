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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.update
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

    fun initVm(recipe: FoodRecipe) {
        viewModelScope.launch {
            val isFavored = getFavoredRecipe(recipe.id) != null

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
        recipeId: Int
    ): FavoriteEntity? {

        var recipe: FavoriteEntity? = null
        viewModelScope.launch {
            val favoritesList =
                repository.localDataSource.loadFavoriteRecipes().lastOrNull()
            recipe = favoritesList?.firstOrNull { entity -> entity.recipe.id == recipeId }
        }

        return recipe
    }

    override fun messageShown(messageId: Long) {
        _uiState.update { currentUiState ->
            val messages = currentUiState.userMessages.filterNot { it.id == messageId }
            currentUiState.copy(userMessages = messages)
        }
    }

    fun toggleIsFavored() {
        viewModelScope.launch {

            with(_uiState.value) {
                if (!isFavored) {
                    writeFavoriteRecipe(
                        FavoriteEntity(
                            0,
                            currentRecipe.id,
                            currentRecipe
                        )
                    )
                    addMessageToQueue(
                        getString(R.string.scr_details_tst_recipe_saved_to_favorites)
                    )

                } else {

                    deleteFavoriteRecipe(currentRecipe.id.toLong())

                    addMessageToQueue(
                        getString(R.string.scr_details_tst_recipe_removed_from_favorites)
                    )
                }

                _uiState.update { state -> state.copy(isFavored = !state.isFavored) }
            }
        }
    }

    private suspend fun writeFavoriteRecipe(recipe: FavoriteEntity) =
        repository.localDataSource.insertFavoriteRecipe(recipe)

    private suspend fun deleteFavoriteRecipe(recipeId: Long) =
        repository.localDataSource.deleteGroupOfFavoriteRecipes(listOf(recipeId))

}