package com.example.calpro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class định nghĩa các màn hình trong app
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Calculator : Screen(
        route = "calculator",
        title = "Calculator",
        icon = Icons.Default.Calculate
    )

    object History : Screen(
        route = "history",
        title = "History",
        icon = Icons.Default.History
    )

    object Converter : Screen(
        route = "converter",
        title = "Converter",
        icon = Icons.Default.SwapHoriz
    )



    object About : Screen(
        route = "about",
        title = "About",
        icon = Icons.Default.Info
    )

    object UserGuide : Screen(
        route = "user_guide",
        title = "User Guide",
        icon = Icons.Default.Book
    )

    object Settings : Screen(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings
    )
}
