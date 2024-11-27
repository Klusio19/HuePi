package com.klusio19.huepi.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.klusio19.huepi.presentation.screens.home.HomeScreen
import com.klusio19.huepi.presentation.screens.home.HomeViewModel
import com.klusio19.huepi.presentation.screens.loading.LoadingScreen
import com.klusio19.huepi.presentation.screens.loading.LoadingViewModel
import com.klusio19.huepi.presentation.screens.setup_and_connect.SetupAndConnectScreen
import com.klusio19.huepi.presentation.screens.setup_and_connect.SetupAndConnectViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SetupNavGraph(startDestination: String, navController: NavHostController, context: Application) {
    NavHost(
        startDestination = startDestination,
        navController = navController
    ) {
        loadingRoute(
            context = context,
            navController = navController
        )
        setupAndConnectRoute(navController)
        homeRoute()
        lightRoute()
    }
}

fun NavGraphBuilder.loadingRoute(
    context: Application,
    navController: NavHostController,
) {
    composable(
        route = Screen.Loading.route,
    ) {
        val viewModel = LoadingViewModel(context)

        LaunchedEffect(key1 = true) {
            viewModel.navigationRoute.collectLatest { route ->
                route?.let {
                    navController.navigate(route) {
                        popUpTo(Screen.Loading.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }

        LoadingScreen()
    }
}


fun NavGraphBuilder.setupAndConnectRoute(navController: NavHostController) {
    composable(
        route = Screen.SetupAndConnect.route,
    ) {
        val viewModel: SetupAndConnectViewModel = viewModel()

        LaunchedEffect(Unit) {
            viewModel.navigationEvent.collect { shouldNavigate ->
                if (shouldNavigate) {
                    delay(1000L)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SetupAndConnect.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }

        SetupAndConnectScreen(
            onIpChange = { ip -> viewModel.updateIpNumbersTextValue(ip) },
            onApiKeyChange = { apiKey -> viewModel.updateApiKeyTextValue(apiKey) },
            onValidateInputs = { viewModel.validateInputs() },
            onConnectClicked = {
                viewModel.raspiConnectionEstablished()
            },
            isIpAddressValid = viewModel.isIpAddressValid,
            isApiKeyValid = viewModel.isApiKeyValid,
            ipText = viewModel.ipNumbersTextValue,
            apiKeyText = viewModel.apiKeyTextValue,
            connectionState = viewModel.connectionState
        )
    }
}

fun NavGraphBuilder.homeRoute() {
    composable(
        route = Screen.Home.route,
    ) {
        val viewModel: HomeViewModel = viewModel()
        HomeScreen(
            lightBulbsList = viewModel.lightBulbsList.collectAsState().value,
            isRefreshing = viewModel.isRefreshing.collectAsState().value,
            onRefresh = { viewModel.fetchLightBulbs() },
            onLightBulbClicked = { lightId ->
                // Handle light bulb click
            }
        )

    }
}

fun NavGraphBuilder.lightRoute() {
    composable(
        route = Screen.Light.route,
        arguments = listOf(navArgument(name = "lightId") {
            type = NavType.StringType
            nullable = false
        })
    ) {

    }
}
