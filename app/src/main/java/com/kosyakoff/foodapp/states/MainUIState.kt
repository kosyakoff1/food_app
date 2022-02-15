package com.kosyakoff.foodapp.states

data class MainUIState(val userMessages: List<UserMessage>,
                       var networkIsAvailable: Boolean,
                       var backOnline: Boolean)