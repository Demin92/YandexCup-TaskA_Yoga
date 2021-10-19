package ru.demin.taska_yoga

data class ViewState(
    val isTrainingStarted: Boolean = false,
    val volume: Int = 0,
    val isAverageNoiseMeasuring: Boolean = false
)