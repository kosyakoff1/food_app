package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.data.Repository
import com.kosyakoff.foodapp.states.FavoritesUIState
import com.kosyakoff.foodapp.states.UserMessage
import com.kosyakoff.foodapp.ui.base.BaseViewModel
import com.kosyakoff.foodapp.util.extensions.getString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) :
    BaseViewModel(application) {

    private val _uiState: MutableStateFlow<FavoritesUIState> =
        MutableStateFlow(
            FavoritesUIState(userMessages = emptyList(), favorites = emptyList())
        )
    val uiState = _uiState.asStateFlow()

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

    init {
        repository.localDataSource.loadFavoriteRecipes().onEach { favoriteEntities ->
            _uiState.update {
                it.copy(favorites = favoriteEntities)
            }
        }.launchIn(viewModelScope)
    }

    fun deleteAllFavoriteRecipes() = viewModelScope.launch {
        repository.localDataSource.deleteAllFavoriteRecipes()
        addMessageToQueue(getString(R.string.scr_favorites_all_favorites_deleted))
    }

    fun deleteGroupOfFavoriteRecipes(group: List<Long>) =
        viewModelScope.launch {
            repository.localDataSource.deleteGroupOfFavoriteRecipes(group)
            addMessageToQueue(getString(R.string.scr_details_tst_recipes_removed_from_favorites))
        }
}