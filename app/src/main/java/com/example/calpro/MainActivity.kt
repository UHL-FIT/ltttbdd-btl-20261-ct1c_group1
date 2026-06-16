package com.example.calpro

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.calpro.ui.navigation.NavGraph
import com.example.calpro.ui.navigation.Screen
import com.example.calpro.ui.theme.CalcProTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "Lifecycle"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: MainActivity được khởi tạo, chuẩn bị thiết lập UI")

        val app = applicationContext as CalcApplication
        val themeRepo = app.appContainer.themeRepository

        setContent {
            val themeMode by themeRepo.themeModeFlow.collectAsStateWithLifecycle(
                initialValue = com.example.calpro.data.repository.ThemeMode.SYSTEM
            )

            val isDarkTheme = when (themeMode) {
                com.example.calpro.data.repository.ThemeMode.DARK -> true
                com.example.calpro.data.repository.ThemeMode.LIGHT -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            CalcProTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                // Danh sách các mục trong Drawer
                val drawerItems = listOf(
                    Screen.Calculator,
                    Screen.History,
                    Screen.Converter,
                    Screen.About
                )

                // Tìm title của màn hình hiện tại
                val currentScreen = drawerItems.find { it.route == currentRoute }
                val currentTitle = when (currentScreen) {
                    Screen.Calculator -> stringResource(R.string.calculator)
                    Screen.History -> stringResource(R.string.history)
                    Screen.Converter -> stringResource(R.string.converter)
                    Screen.About -> stringResource(R.string.about)
                    else -> stringResource(R.string.app_name)
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.fillMaxWidth(0.77f)
                        ) {
                            // Header của Drawer
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.app_name),
                                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.drawer_subtitle),
                                modifier = Modifier.padding(horizontal = 28.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Các mục menu
                            drawerItems.forEach { screen ->
                                val label = when (screen) {
                                    Screen.Calculator -> stringResource(R.string.calculator)
                                    Screen.History -> stringResource(R.string.history)
                                    Screen.Converter -> stringResource(R.string.converter)
                                    Screen.About -> stringResource(R.string.about)
                                    else -> screen.title
                                }

                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = label
                                        )
                                    },
                                    label = { Text(text = label) },
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            // Không khôi phục state cho màn hình About để tránh bị kẹt ở UserGuide
                                            restoreState = screen.route != Screen.About.route
                                        }
                                    },
                                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                )
                            }
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = currentTitle,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 20.sp
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch { drawerState.open() }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu"
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    ) { innerPadding ->
                        NavGraph(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Activity đang trở nên visible cho người dùng")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Activity đang ở foreground và người dùng có thể tương tác")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Activity đang mất focus, có thể do dialog hoặc activity khác")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Activity không còn visible, đang chuyển sang background")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Activity đang bị hủy, giải phóng tài nguyên")
    }
}

