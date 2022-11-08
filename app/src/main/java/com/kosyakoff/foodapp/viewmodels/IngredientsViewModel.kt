package com.kosyakoff.foodapp.viewmodels

import android.app.Application
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.ui.base.BaseViewModel
import com.kosyakoff.foodapp.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class IngredientsViewModel @Inject constructor(
    application: Application
) : BaseViewModel(application) {

    private val _ingredientsState: MutableStateFlow<NetworkResult<FoodRecipe>> =
        MutableStateFlow(
            NetworkResult.Loading()
        )
    val ingredientsState = _ingredientsState.asStateFlow()

    override fun messageShown(messageId: Long) {
        TODO("Not yet implemented")
    }

    override fun addMessageToQueue(message: String) {
        TODO("Not yet implemented")
    }

    fun initVm(recipe: FoodRecipe) {
        _ingredientsState.update { NetworkResult.Loading() }
        _ingredientsState.update { NetworkResult.Success(recipe) }
    }
}