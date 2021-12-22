package com.kosyakoff.foodapp.data

import com.kosyakoff.foodapp.data.database.RecipesDao
import com.kosyakoff.foodapp.data.database.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {
    suspend fun insertRecipes(recipesEntity: RecipesEntity) =
        recipesDao.insertRecipes(recipesEntity)

    fun readFromDatabase(): Flow<List<RecipesEntity>> = recipesDao.readRecipes()
}