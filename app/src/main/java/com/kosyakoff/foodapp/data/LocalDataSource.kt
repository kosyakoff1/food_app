package com.kosyakoff.foodapp.data

import com.kosyakoff.foodapp.data.database.RecipesDao
import com.kosyakoff.foodapp.data.database.entities.FavoriteEntity
import com.kosyakoff.foodapp.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {
    fun loadRecipes(): Flow<List<RecipesEntity>> = recipesDao.loadRecipes()

    fun loadFavoriteRecipes(): Flow<List<FavoriteEntity>> = recipesDao.loadFavoriteRecipes()

    suspend fun insertFavoriteRecipe(favoriteEntity: FavoriteEntity) {
        recipesDao.insertFavoriteRecipe(favoriteEntity)
    }

    suspend fun insertRecipes(recipesEntity: RecipesEntity) =
        recipesDao.insertRecipes(recipesEntity)

    suspend fun deleteFavoriteRecipe(recipe: FavoriteEntity) =
        recipesDao.deleteFavoriteRecipe(recipe)

    suspend fun deleteAllFavoriteRecipes() = recipesDao.deleteAllFavoriteRecipes()

}