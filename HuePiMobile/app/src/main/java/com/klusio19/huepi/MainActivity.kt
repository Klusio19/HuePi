package com.klusio19.huepi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.klusio19.huepi.navigation.Screen
import com.klusio19.huepi.navigation.SetupNavGraph
import com.klusio19.huepi.ui.theme.HuePiTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            HuePiTheme {
                val navController = rememberNavController()
                    SetupNavGraph(
                        startDestination = Screen.Loading,
                        navController = navController,
                        context = application
                    )
            }
        }
    }
}
