package com.kosyakoff.foodapp.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kosyakoff.foodapp.models.FoodRecipe
import com.kosyakoff.foodapp.models.FoodRecipes

class RecipesTypeConverter {

    private var gson = Gson()

    @TypeConverter
    fun foodRecipesToString(foodRecipes: FoodRecipes): String = gson.toJson(foodRecipes)

    @TypeConverter
    fun stringToFoodRecipes(data: String): FoodRecipes {
        val listType = object : TypeToken<FoodRecipes>() {}.type
        return gson.fromJson<FoodRecipes>(data, listType)
    }

    @TypeConverter
    fun foodRecipeToString(foodRecipe: FoodRecipe): String = gson.toJson(foodRecipe)

    @TypeConverter
    fun stringToFoodRecipe(data: String): FoodRecipe {
        val listType = object : TypeToken<FoodRecipe>() {}.type
        return gson.fromJson<FoodRecipe>(data, listType)
    }

}