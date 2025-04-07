package com.one.last.commit.holdup

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.one.last.commit.holdup.ui.AppSelectionScreen
import com.one.last.commit.holdup.ui.PermissionScreen
import com.one.last.commit.holdup.ui.SplashScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Permission : Screen("permission")
    data object AppSelection : Screen("app_selection")
}

@Composable
fun HoldUpNavHost(
    navController: NavHostController,
) {
    Scaffold(
        containerColor = Color.Black
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Screen.Splash.route,
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    navigateToPermission = {
                        navController.navigate(Screen.Permission.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    navigateToAppSelection = {
                        navController.navigate(Screen.AppSelection.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(Screen.Permission.route) {
                PermissionScreen(
                    navigateToAppSelection = {
                        navController.navigate(Screen.AppSelection.route) {
                            popUpTo(Screen.Permission.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(Screen.AppSelection.route) {
                AppSelectionScreen()
            }
        }
    }
}