package com.kosyakoff.foodapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.kosyakoff.foodapp.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.kosyakoff.foodapp.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.kosyakoff.foodapp.util.Constants.Companion.PREFERENCES_DIET_TYPE_ID_KEY
import com.kosyakoff.foodapp.util.Constants.Companion.PREFERENCES_DIET_TYPE_KEY
import com.kosyakoff.foodapp.util.Constants.Companion.PREFERENCES_FILE_NAME
import com.kosyakoff.foodapp.util.Constants.Companion.PREFERENCES_MEAL_TYPE_ID_KEY
import com.kosyakoff.foodapp.util.Constants.Companion.PREFERENCES_MEAL_TYPE_KEY
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_FILE_NAME)

    private object PreferenceKeys {
        val mealType = stringPreferencesKey(PREFERENCES_MEAL_TYPE_KEY)
        val mealTypeId = intPreferencesKey(PREFERENCES_MEAL_TYPE_ID_KEY)

        val dietType = stringPreferencesKey(PREFERENCES_DIET_TYPE_KEY)
        val dietTypeId = intPreferencesKey(PREFERENCES_DIET_TYPE_ID_KEY)

    }

    val readMealAndDietType: Flow<MealAndDietType> = context.dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        val selectedMealType = preferences[PreferenceKeys.mealType] ?: DEFAULT_MEAL_TYPE
        val selectedMealTypeId = preferences[PreferenceKeys.mealTypeId] ?: 0
        val selectedDietType = preferences[PreferenceKeys.dietType] ?: DEFAULT_DIET_TYPE
        val selectedDietTypeId = preferences[PreferenceKeys.dietTypeId] ?: 0

        MealAndDietType(
            selectedMealType,
            selectedMealTypeId, selectedDietType,
            selectedDietTypeId
        )
    }

    suspend fun saveMealAndDietTypes(
        selectedMealType: String,
        selectedMealTypeId: Int,
        selectedDietType: String,
        selectedDietTypeId: Int
    ) {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.mealType] = selectedMealType
            settings[PreferenceKeys.mealTypeId] = selectedMealTypeId
            settings[PreferenceKeys.dietType] = selectedDietType
            settings[PreferenceKeys.dietTypeId] = selectedDietTypeId
        }
    }

}

data class MealAndDietType(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int
)