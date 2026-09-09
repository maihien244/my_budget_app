package com.example.mybudget.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mybudget.ui.components.AnimatedBottomNavBar
import com.example.mybudget.ui.screens.AddExpenseScreen
import com.example.mybudget.ui.screens.AnalyticsScreen
import com.example.mybudget.ui.screens.HomeScreen
import com.example.mybudget.ui.screens.SettingsScreen
import com.example.mybudget.viewmodel.ExpenseViewModel

object Screen {
    const val HOME = "home"
    const val ADD_EXPENSE = "add_expense"
    const val ANALYTICS = "analytics"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation(
    viewModel: ExpenseViewModel,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.HOME

    Scaffold(
        bottomBar = {
            AnimatedBottomNavBar(
                currentRoute = currentRoute,
                onNavigateToRoute = { route ->
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.HOME,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(180)) },
            exitTransition = { fadeOut(animationSpec = tween(180)) },
            popEnterTransition = { fadeIn(animationSpec = tween(180)) },
            popExitTransition = { fadeOut(animationSpec = tween(180)) }
        ) {
            composable(Screen.HOME) {
                HomeScreen(viewModel = viewModel)
            }
            composable(Screen.ADD_EXPENSE) {
                AddExpenseScreen(
                    viewModel = viewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.HOME) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.ANALYTICS) {
                AnalyticsScreen(viewModel = viewModel)
            }
            composable(Screen.SETTINGS) {
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}

