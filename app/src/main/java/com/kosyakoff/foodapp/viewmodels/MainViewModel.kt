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
import com.kosyakoff.foodapp.models.FoodRecipes
import com.kosyakoff.foodapp.ui.base.BaseViewModel
import com.kosyakoff.foodapp.util.NetworkListener
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
    private val dataStoreRepository: DataStoreRepository,
    private val networkListener: NetworkListener,
    application: Application
) : BaseViewModel(application) {

    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    var networkIsAvailable = false
    var backOnline = false

    private fun saveBackOnline(backOnline: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }
    }

    private fun showNetworkStatus(newNetworkStatus: Boolean?) {
        newNetworkStatus?.let { networkIsAvailable = it }

        if (!networkIsAvailable) {
            appContext.showToast(getString(R.string.str_error_no_internet_connection))
            saveBackOnline(true)
        } else if (backOnline) {
            appContext.showToast(getString(R.string.str_internet_restored))
            saveBackOnline(false)
        }
    }

    fun initVm() {

        viewModelScope.launch {
            networkListener.checkNetworkAvailability().collect { status ->
                showNetworkStatus(status)
            }
        }
    }

    override fun messageShown(messageId: Long) {
        TODO("Not yet implemented")
    }

    override fun addMessageToQueue(message: String) {
        TODO("Not yet implemented")
    }

}