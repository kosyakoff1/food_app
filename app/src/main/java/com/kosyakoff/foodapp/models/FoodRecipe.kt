package com.kosyakoff.foodapp.models


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodRecipe(
    @Expose
    @SerializedName("id")
    val id: Int = 0,
    @Expose
    @SerializedName("title")
    val title: String = "",
    @Expose
    @SerializedName("image")
    val image: String? = null,
    @Expose
    @SerializedName("imageType")
    val imageType: String? = null,
    @Expose
    @SerializedName("servings")
    val servings: Int = 0,
    @Expose
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int = 0,
    @Expose
    @SerializedName("license")
    val license: String? = null,
    @Expose
    @SerializedName("sourceName")
    val sourceName: String? = null,
    @Expose
    @SerializedName("sourceUrl")
    val sourceUrl: String? = null,
    @Expose
    @SerializedName("spoonacularSourceUrl")
    val spoonacularSourceUrl: String? = null,
    @Expose
    @SerializedName("aggregateLikes")
    val aggregateLikes: Int = 0,
    @Expose
    @SerializedName("healthScore")
    val healthScore: Double = 0.0,
    @Expose
    @SerializedName("spoonacularScore")
    val spoonacularScore: Double = 0.0,
    @Expose
    @SerializedName("pricePerServing")
    val pricePerServing: Double = 0.0,

    ///TODO: add analyzedInstructions with addition to web instructions
//    @Expose
//    @SerializedName("analyzedInstructions")
//    val analyzedInstructions: List<String> = emptyList(),
    @Expose
    @SerializedName("cheap")
    val cheap: Boolean = false,
    @Expose
    @SerializedName("creditsText")
    val creditsText: String? = null,
    @Expose
    @SerializedName("cuisines")
    val cuisines: List<String> = emptyList(),
    @Expose
    @SerializedName("dairyFree")
    val dairyFree: Boolean = false,
    @Expose
    @SerializedName("diets")
    val diets: List<String> = emptyList(),
    @Expose
    @SerializedName("gaps")
    val gaps: String? = null,
    @Expose
    @SerializedName("glutenFree")
    val glutenFree: Boolean = false,
    @Expose
    @SerializedName("instructions")
    val instructions: String? = null,
    @Expose
    @SerializedName("ketogenic")
    val ketogenic: Boolean = false,
    @Expose
    @SerializedName("lowFodmap")
    val lowFodmap: Boolean = false,
    @Expose
    @SerializedName("occasions")
    val occasions: List<String> = emptyList(),
    @Expose
    @SerializedName("sustainable")
    val sustainable: Boolean = false,
    @Expose
    @SerializedName("vegan")
    val vegan: Boolean = false,
    @Expose
    @SerializedName("vegetarian")
    val vegetarian: Boolean = false,
    @Expose
    @SerializedName("veryHealthy")
    val veryHealthy: Boolean = false,
    @Expose
    @SerializedName("veryPopular")
    val veryPopular: Boolean = false,
    @Expose
    @SerializedName("whole30")
    val whole30: Boolean = false,
    @Expose
    @SerializedName("weightWatcherSmartPoints")
    val weightWatcherSmartPoints: Int = 0,
    @Expose
    @SerializedName("dishTypes")
    val dishTypes: List<String> = emptyList(),
    @Expose
    @SerializedName("extendedIngredients")
    val extendedIngredients: List<ExtendedIngredients> = emptyList(),
    @Expose
    @SerializedName("summary")
    val summary: String? = null,
    @Expose
    @SerializedName("winePairing")
    val winePairing: WinePairing? = null
) : Parcelable