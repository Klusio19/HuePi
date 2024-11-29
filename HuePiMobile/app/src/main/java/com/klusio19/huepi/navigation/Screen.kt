package com.klusio19.huepi.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Loading : Screen()

    @Serializable
    data object SetupAndConnect : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data class LightDetails(val rid: String) : Screen()
}
