package com.kosyakoff.foodapp.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Us(
    @Expose
    @SerializedName("amount")
    val amount: Double,
    @Expose
    @SerializedName("unitLong")
    val unitLong: String,
    @Expose
    @SerializedName("unitShort")
    val unitShort: String
) : Parcelable