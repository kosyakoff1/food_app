package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.data.DataStoreRepository
import com.kosyakoff.foodapp.states.MainUIState
import com.kosyakoff.foodapp.states.UserMessage
import com.kosyakoff.foodapp.ui.base.BaseViewModel
import com.kosyakoff.foodapp.util.NetworkListener
import com.kosyakoff.foodapp.util.extensions.getString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val networkListener: NetworkListener,
    application: Application
) : BaseViewModel(application) {

    private val _uiState =
        MutableStateFlow(
            MainUIState(
                userMessages = emptyList(),
                networkIsAvailable = false,
                backOnline = false
            )
        )
    val uiState: StateFlow<MainUIState> = _uiState

    private fun saveBackOnline(backOnline: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.saveBackOnline(backOnline)
        }
    }

    private fun showNetworkStatus(newNetworkStatus: Boolean?) {

        newNetworkStatus?.let {
            _uiState.update { currentState ->
                currentState.copy(
                    networkIsAvailable = it,
                )
            }
        }

        if (!_uiState.value.networkIsAvailable) {
            addMessageToQueue(getString(R.string.str_error_no_internet_connection))
            saveBackOnline(true)
        } else if (_uiState.value.backOnline) {
            addMessageToQueue(getString(R.string.str_internet_restored))
            saveBackOnline(false)
        }
    }

    fun initVm() {

        networkListener.checkNetworkAvailability().onEach { status ->
            showNetworkStatus(status)
        }.launchIn(viewModelScope)

        dataStoreRepository.readBackOnline.onEach {
            _uiState.update { currentState -> currentState.copy(backOnline = it) }
        }.launchIn(viewModelScope)
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