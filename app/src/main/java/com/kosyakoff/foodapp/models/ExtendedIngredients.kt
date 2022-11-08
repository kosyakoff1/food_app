package com.kosyakoff.foodapp.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExtendedIngredients(
    @Expose
    @SerializedName("aisle")
    val aisle: String? = null,
    @Expose
    @SerializedName("amount")
    val amount: Double = 0.0,
    @Expose
    @SerializedName("consitency")
    val consitency: String? = null,
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("image")
    val image: String? = null,
    @Expose
    @SerializedName("measures")
    val measures: Measures,
    @Expose
    @SerializedName("meta")
    val meta: List<String> = emptyList(),
    @Expose
    @SerializedName("name")
    val name: String?,
    @Expose
    @SerializedName("original")
    val original: String? = null,
    @Expose
    @SerializedName("originalName")
    val originalName: String? = null,
    @Expose
    @SerializedName("unit")
    val unit: String? = null
) : Parcelable