package com.kosyakoff.foodapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kosyakoff.foodapp.models.FoodRecipes
import com.kosyakoff.foodapp.util.Constants.Companion.RECIPES_TABLE_NAME

@Entity(tableName = RECIPES_TABLE_NAME)
data class RecipesEntity(
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    var foodRecipes: FoodRecipes
)