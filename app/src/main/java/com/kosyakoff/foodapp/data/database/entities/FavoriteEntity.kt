package com.kosyakoff.foodapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.util.Constants.Companion.FAVORITE_RECIPES_TABLE_NAME

@Entity(tableName = FAVORITE_RECIPES_TABLE_NAME)
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var recipe_id: Int,
    var recipe: FoodRecipe
)