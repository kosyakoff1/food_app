package com.kosyakoff.foodapp.util

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel

fun AndroidViewModel.getString(@StringRes resId: Int): String = getApplication<Application>()
    .getString(resId)