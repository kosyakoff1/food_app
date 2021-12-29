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
    suspend fun insertFavoriteRecipe(recipe: FavoriteEntity)

    @Query("select * from recipes_table order by id ASC")
    fun loadRecipes(): Flow<List<RecipesEntity>>

    @Query("select * from favorites_table order by id asc")
    fun loadFavoriteRecipes(): Flow<List<FavoriteEntity>>

    @Delete
    suspend fun deleteFavoriteRecipe(recipe: FavoriteEntity)

    @Query("delete from favorites_table")
    suspend fun deleteAllFavoriteRecipes()

}