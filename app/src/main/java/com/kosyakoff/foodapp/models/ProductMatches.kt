package com.kosyakoff.foodapp.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductMatches(
    @Expose
    @SerializedName("id")
    val id: Int,
    @Expose
    @SerializedName("title")
    val title: String,
    @Expose
    @SerializedName("description")
    val description: String,
    @Expose
    @SerializedName("price")
    val price: String,
    @Expose
    @SerializedName("imageUrl")
    val imageUrl: String,
    @Expose
    @SerializedName("averageRating")
    val averageRating: Double,
    @Expose
    @SerializedName("ratingCount")
    val ratingCount: Int,
    @Expose
    @SerializedName("score")
    val score: Double,
    @Expose
    @SerializedName("link")
    val link: String
) : Parcelable