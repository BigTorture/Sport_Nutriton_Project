package com.example.myapplication.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.AppPalette
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.ProfileScreen
import com.example.myapplication.ui.ProductListScreen
import com.example.myapplication.ui.auth.AuthScreen
import com.example.myapplication.ui.dashboard.DashboardScreen
import com.example.myapplication.ui.diary.DiaryScreen
import com.example.myapplication.ui.reference.ReferenceScreen
import com.example.myapplication.ui.reminders.RemindersScreen
import com.example.myapplication.ui.reports.ReportsScreen
import com.example.myapplication.ui.water.WaterTrackerScreen
import com.example.myapplication.utils.IconUtils
import com.example.myapplication.viewmodel.NutritionAppViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Главная", IconUtils.SafeHome)
    object Diary : Screen("diary", "Дневник", IconUtils.SafeBook)
    object Products : Screen("products", "Каталог", IconUtils.SafeMenu)
    object Reminders : Screen("reminders", "Напоминания", IconUtils.SafeNotifications)
    object Profile : Screen("profile", "Профиль", IconUtils.SafePerson)
}

private object ExtraRoute {
    const val Water = "water"
    const val Reports = "reports"
    const val Reference = "reference"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(viewModel: NutritionAppViewModel) {
    val appState by viewModel.appState.collectAsState()
    if (appState.activeProfileId == null) {
        AuthScreen(viewModel)
        return
    }
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.background(AppPalette.BackgroundGradient),
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onOpenDiary = { navController.navigate(Screen.Diary.route) },
                    onOpenWater = { navController.navigate(ExtraRoute.Water) },
                    onOpenReports = { navController.navigate(ExtraRoute.Reports) },
                    onOpenReference = { navController.navigate(ExtraRoute.Reference) }
                )
            }
            composable(Screen.Diary.route) {
                DiaryScreen(viewModel)
            }
            composable(Screen.Products.route) {
                ProductListScreen()
            }
            composable(Screen.Reminders.route) {
                RemindersScreen(viewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(viewModel)
            }
            composable(ExtraRoute.Water) {
                WaterTrackerScreen(viewModel)
            }
            composable(ExtraRoute.Reports) {
                ReportsScreen(viewModel)
            }
            composable(ExtraRoute.Reference) {
                ReferenceScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Dashboard,
        Screen.Diary,
        Screen.Products,
        Screen.Reminders,
        Screen.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = AppPalette.CardSurface,
        tonalElevation = 12.dp
    ) {
        items.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(text = screen.title)
                },
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppPalette.AccentMint,
                    selectedTextColor = AppPalette.AccentMint,
                    unselectedIconColor = AppPalette.TextSecondary,
                    unselectedTextColor = AppPalette.TextSecondary,
                    indicatorColor = AppPalette.CardAltSurface
                ),
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
