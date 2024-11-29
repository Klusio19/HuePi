package com.klusio19.huepi.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.klusio19.huepi.presentation.screens.home.HomeScreen
import com.klusio19.huepi.presentation.screens.home.HomeViewModel
import com.klusio19.huepi.presentation.screens.light_details.LightDetailsScreen
import com.klusio19.huepi.presentation.screens.light_details.LightDetailsViewModel
import com.klusio19.huepi.presentation.screens.loading.LoadingScreen
import com.klusio19.huepi.presentation.screens.loading.LoadingViewModel
import com.klusio19.huepi.presentation.screens.setup_and_connect.SetupAndConnectScreen
import com.klusio19.huepi.presentation.screens.setup_and_connect.SetupAndConnectViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SetupNavGraph(startDestination: Screen, navController: NavHostController, context: Application) {
    NavHost(navController = navController, startDestination = startDestination) {
        loadingRoute(context, navController)
        setupAndConnectRoute(navController)
        homeRoute(navController)
        lightRoute(context, navController)
    }
}

fun NavGraphBuilder.loadingRoute(context: Application, navController: NavHostController) {
    composable<Screen.Loading> {
        val viewModel = LoadingViewModel(context)

        LaunchedEffect(Unit) {
            viewModel.navigationRoute.collectLatest { route ->
                route?.let {
                    navController.popBackStack()
                    navController.navigate(it)
                }
            }
        }
        LoadingScreen()
    }
}

fun NavGraphBuilder.setupAndConnectRoute(navController: NavHostController) {
    composable<Screen.SetupAndConnect> {
        val viewModel: SetupAndConnectViewModel = viewModel()
        LaunchedEffect(Unit) {
            viewModel.navigationEvent.collect { shouldNavigate ->
                if (shouldNavigate) {
                    delay(1000L)
                    navController.navigate(Screen.Home)
                }
            }
        }
        SetupAndConnectScreen(
            onIpChange = { viewModel.updateIpNumbersTextValue(it) },
            onApiKeyChange = { viewModel.updateApiKeyTextValue(it) },
            onValidateInputs = { viewModel.validateInputs() },
            onConnectClicked = { viewModel.raspiConnectionEstablished() },
            isIpAddressValid = viewModel.isIpAddressValid,
            isApiKeyValid = viewModel.isApiKeyValid,
            ipText = viewModel.ipNumbersTextValue,
            apiKeyText = viewModel.apiKeyTextValue,
            connectionState = viewModel.connectionState
        )
    }
}

fun NavGraphBuilder.homeRoute(navController: NavHostController) {
    composable<Screen.Home> {
        val viewModel: HomeViewModel = viewModel()
        HomeScreen(
            lightBulbsList = viewModel.lightBulbsList.collectAsState().value,
            isRefreshing = viewModel.isRefreshing.collectAsState().value,
            onRefresh = { viewModel.fetchLightBulbs() },
            onLightBulbClicked = { lightId -> navController.navigate(Screen.LightDetails(lightId)) }
        )
    }
}

fun NavGraphBuilder.lightRoute(context: Application, navController: NavHostController) {
    composable<Screen.LightDetails> { backStackEntry ->
        val args = backStackEntry.toRoute<Screen.LightDetails>()
        val viewModel = LightDetailsViewModel(context, args.rid)
        LightDetailsScreen(args.rid)
    }
}
