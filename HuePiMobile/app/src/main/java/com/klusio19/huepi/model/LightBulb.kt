package com.klusio19.huepi.model

data class LightBulb(
    val rid: String,
    val brightness: Float,
    val color: String,
    val isOn: Boolean,
    val name: String,
    val taskRunning: Boolean
)