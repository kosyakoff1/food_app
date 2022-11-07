package com.kosyakoff.foodapp.util.extensions

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel

fun AndroidViewModel.getString(@StringRes resId: Int): String = getApplication<Application>()
    .getString(resId)

val AndroidViewModel.appContext: Context get() = getApplication<Application>()