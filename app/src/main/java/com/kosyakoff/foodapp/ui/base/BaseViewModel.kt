package com.kosyakoff.foodapp.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    abstract fun userMessageShown(messageId: Long)
    abstract fun addMessageToQueue(message: String)
}