package com.kosyakoff.foodapp.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WinePairing(
    @Expose
    @SerializedName("pairedWines")
    val pairedWines: List<String>,
    @Expose
    @SerializedName("pairingText")
    val pairingText: String,
    @Expose
    @SerializedName("productMatches")
    val productMatches: List<ProductMatches>
) : Parcelable