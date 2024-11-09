package com.klusio19.huepi.navigation

sealed class Screen(val route: String) {
    object Loading: Screen(route = "loading_screen")
    object SetupAndConnect: Screen(route = "setup_and_connect_screen")
    object Home: Screen(route = "home_screen")
    object Light: Screen(route = "light_screen/{lightId}")
}