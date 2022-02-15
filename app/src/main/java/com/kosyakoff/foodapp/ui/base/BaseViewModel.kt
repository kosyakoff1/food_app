package com.kosyakoff.foodapp.ui.base

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import com.kosyakoff.foodapp.R
import com.kosyakoff.foodapp.util.NetworkResult
import com.kosyakoff.foodapp.util.extensions.appContext
import com.kosyakoff.foodapp.util.extensions.getString
import retrofit2.Response

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    abstract fun messageShown(messageId: Long)
    abstract fun addMessageToQueue(message: String)

    protected fun <T> handleServerResponse(response: Response<T>): NetworkResult<T> {
        when {
            response.message().contains("timeout") -> {
                return NetworkResult.Error(getString(R.string.str_error_timeout))
            }
            response.code() == 402 -> {
                return NetworkResult.Error(getString(R.string.str_error_api_limit_reached))
            }
            response.isSuccessful -> {
                val data = response.body()!!

                return NetworkResult.Success(data)
            }
            else -> {
                return NetworkResult.Error(response.message().toString())
            }
        }
    }

    protected fun hasInternetConnection(): Boolean {
        val connectivityManager =
            appContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false

        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}