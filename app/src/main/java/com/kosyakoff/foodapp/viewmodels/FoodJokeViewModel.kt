package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.data.Repository
import com.kosyakoff.foodapp.models.FoodJoke
import com.kosyakoff.foodapp.ui.base.BaseViewModel
import com.kosyakoff.foodapp.util.Constants.Companion.API_KEY
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.util.extensions.getString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodJokeViewModel @Inject constructor(
    application: Application,
    private val repository: Repository
) : BaseViewModel(application) {
    override fun messageShown(messageId: Long) {
        TODO("Not yet implemented")
    }

    override fun addMessageToQueue(message: String) {
        TODO("Not yet implemented")
    }

    private val _foodJokeState: MutableStateFlow<NetworkResult<FoodJoke>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val foodJokeState = _foodJokeState.asStateFlow()

    fun initVm() {
        getFoodJoke(API_KEY)
    }

    private fun getFoodJoke(apiKey: String) = viewModelScope.launch {
        fetchFoodJokeSafeCall(apiKey)
    }

    private suspend fun fetchFoodJokeSafeCall(apiKey: String) {
        _foodJokeState.value = NetworkResult.Loading()

        if (hasInternetConnection()) {
            try {
                val response = repository.remoteDataSource.getFoodJoke(apiKey)
                _foodJokeState.value = handleServerResponse(response)

            } catch (e: Exception) {
                _foodJokeState.value =
                    NetworkResult.Error(e.message)
            }
        } else {
            _foodJokeState.value =
                NetworkResult.Error(
                    getString(R.string.str_error_no_internet_connection)
                )
        }
    }
}