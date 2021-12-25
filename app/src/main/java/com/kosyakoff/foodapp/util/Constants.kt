package com.kosyakoff.foodapp.util

class Constants {

    companion object {
        const val API_KEY = "086a13baf0c84117bd6ea1d16c585d40"
        const val BASE_URL = "https://api.spoonacular.com"

        //API query keys
        const val QUERY_NUMBER = "number"
        const val QUERY_API_KEY = "apiKey"
        const val QUERY_TYPE = "type"
        const val QUERY_DIET = "diet"
        const val QUERY_RECIPE_INFO = "addRecipeInformation"
        const val QUERY_FILL_INGREDIENTS = "fillIngredients"

        //Room database
        const val RECIPES_DATABASE_NAME = "recipes_database"
        const val RECIPES_TABLE_NAME = "recipes_table"

        //Bottom sheet preferences

        const val PREFERENCES_FILE_NAME = "settings"

        const val DEFAULT_RECIPES_NUMBER = 50L.toString()
        const val DEFAULT_MEAL_TYPE = "main course"
        const val DEFAULT_DIET_TYPE = "vegetarian"
        const val PREFERENCES_MEAL_TYPE_KEY = "mealType"
        const val PREFERENCES_MEAL_TYPE_ID_KEY = "mealTypeId"
        const val PREFERENCES_DIET_TYPE_KEY = "dietType"
        const val PREFERENCES_DIET_TYPE_ID_KEY = "dietTypeId"
        const val PREFERENCES_BACK_ONLINE_KEY = "backOnline"
    }
}