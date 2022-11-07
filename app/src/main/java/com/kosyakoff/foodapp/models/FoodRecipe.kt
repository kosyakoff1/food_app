package com.kosyakoff.foodapp.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.RawValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodRecipe(
    @SerializedName("aggregateLikes")
    val aggregateLikes: Int = 0,
    @SerializedName("cheap")
    val cheap: Boolean = false,
    @SerializedName("dairyFree")
    val dairyFree: Boolean = false,
    @SerializedName("extendedIngredients")
    val extendedIngredients: @RawValue List<ExtendedIngredient> = emptyList(),
    @SerializedName("glutenFree")
    val glutenFree: Boolean = false,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("image")
    val image: String = String(),
    @SerializedName("instructions")
    val instructions: String? = null,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int? = null,
    @SerializedName("sourceUrl")
    val sourceUrl: String = String(),
    @SerializedName("summary")
    val summary: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("vegan")
    val vegan: Boolean = false,
    @SerializedName("vegetarian")
    val vegetarian: Boolean = false,
    @SerializedName("veryHealthy")
    val veryHealthy: Boolean = false,
) : Parcelable