package com.kosyakoff.foodapp.data.database

import androidx.room.*
import com.kosyakoff.foodapp.data.database.entities.FavoriteEntity
import com.kosyakoff.foodapp.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipes(recipe: FavoriteEntity)

    @Query("select * from recipes_table order by id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Query("select * from favorites_table order by id asc")
    fun readFavoriteRecipe(): Flow<List<FavoriteEntity>>

    @Delete
    suspend fun deleteFavoriteRecipe(recipe: FavoriteEntity)

    @Query("delete from favorites_table")
    suspend fun deleteAllFavoriteRecipes()

}