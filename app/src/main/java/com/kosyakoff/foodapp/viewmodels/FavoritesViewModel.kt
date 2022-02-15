package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kosyakoff.FoodApplication
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.data.Repository
import com.kosyakoff.foodapp.data.database.entities.FavoriteEntity
import com.kosyakoff.foodapp.models.FoodJoke
import com.kosyakoff.foodapp.states.FavoritesUIState
import com.kosyakoff.foodapp.states.UserMessage
import com.kosyakoff.foodapp.ui.base.BaseViewModel
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.util.extensions.getString
import com.kosyakoff.foodapp.util.extensions.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            FavoritesUIState(userMessages = emptyList())
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

    val readFavoriteRecipes =
        repository.localDataSource.loadFavoriteRecipes()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteAllFavoriteRecipes() = viewModelScope.launch {
        repository.localDataSource.deleteAllFavoriteRecipes()
        addMessageToQueue(getString(R.string.scr_favorites_all_favorites_deleted))
    }

    fun deleteGroupOfFavoriteRecipes(group: List<Long>) {
        viewModelScope.launch {
            repository.localDataSource.deleteGroupOfFavoriteRecipes(group)
            addMessageToQueue(getString(R.string.scr_details_tst_recipes_removed_from_favorites))
        }
    }
}