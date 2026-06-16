package com.example.calpro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calpro.ui.screens.about.AboutScreen
import com.example.calpro.ui.screens.about.UserGuideScreen
import com.example.calpro.ui.screens.calculator.CalculatorScreen
import com.example.calpro.ui.screens.converter.ConverterScreen
import com.example.calpro.ui.screens.history.HistoryScreen
import com.example.calpro.ui.screens.setting.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Calculator.route,
        modifier = modifier
    ) {
        composable(route = Screen.Calculator.route) {
            CalculatorScreen(navController = navController)
        }

        composable(route = Screen.History.route) {
            HistoryScreen(
                onItemClick = { expr ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "history_expr",
                        expr
                    )
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Converter.route) {
            ConverterScreen()
        }

        composable(route = Screen.About.route) {
            AboutScreen(navController = navController)
        }

        composable(route = Screen.UserGuide.route) {
            UserGuideScreen()
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}