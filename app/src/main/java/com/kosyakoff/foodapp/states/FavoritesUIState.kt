package com.kosyakoff.foodapp.states

import com.kosyakoff.foodapp.data.database.entities.FavoriteEntity

data class FavoritesUIState(
    val userMessages: List<UserMessage>,
    val favorites: List<FavoriteEntity>
)