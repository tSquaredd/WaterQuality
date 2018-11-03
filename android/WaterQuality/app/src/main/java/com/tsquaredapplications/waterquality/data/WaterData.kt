package com.tsquaredapplications.waterquality.data

data class WaterData(
    val flow: Float = 0.0f,
    val temp: Float = 0.0f,
    val pH: Float = 0.0f,
    val tds: Float = 0.0f,
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
    val hour: Int = 0,
    val min: Int = 0,
    val sec: Int = 0
)