package com.kosyakoff.foodapp.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Measures(
    @Expose
    @SerializedName("metric")
    val metric: Metric?,
    @Expose
    @SerializedName("us")
    val us: Us
) : Parcelable