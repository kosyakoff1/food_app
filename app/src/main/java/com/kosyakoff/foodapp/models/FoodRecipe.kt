package com.kosyakoff.foodapp.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.RawValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodRecipe(
    @SerializedName("aggregateLikes")
    val aggregateLikes: Int,
    @SerializedName("cheap")
    val cheap: Boolean,
    @SerializedName("dairyFree")
    val dairyFree: Boolean,
    @SerializedName("extendedIngredients")
    val extendedIngredients: @RawValue List<ExtendedIngredient>,
    @SerializedName("glutenFree")
    val glutenFree: Boolean,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("instructions")
    val instructions: String?,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int?,
    @SerializedName("sourceUrl")
    val sourceUrl: String,
    @SerializedName("summary")
    val summary: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("vegan")
    val vegan: Boolean,
    @SerializedName("vegetarian")
    val vegetarian: Boolean,
    @SerializedName("veryHealthy")
    val veryHealthy: Boolean,
) : Parcelable