package com.kosyakoff.foodapp.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kosyakoff.foodapp.models.FoodRecipes

class RecipesTypeConverter {

    private var gson = Gson()

    @TypeConverter
    fun foodRecipeToString(foodRecipes: FoodRecipes): String = gson.toJson(foodRecipes)

    @TypeConverter
    fun stringToFoodRecipe(data: String): FoodRecipes {
        val listType = object : TypeToken<FoodRecipes>() {}.type
        return gson.fromJson<FoodRecipes>(data, listType)
    }
}