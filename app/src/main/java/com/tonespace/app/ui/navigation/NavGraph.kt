package com.tonespace.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tonespace.app.ui.screens.auth.AuthScreen
import com.tonespace.app.ui.screens.browse.BrowseScreen
import com.tonespace.app.ui.screens.creator.CreatorDashboardScreen
import com.tonespace.app.ui.screens.home.HomeScreen
import com.tonespace.app.ui.screens.player.PlayerScreen
import com.tonespace.app.ui.screens.profile.ProfileScreen
import com.tonespace.app.ui.screens.search.SearchScreen
import com.tonespace.app.ui.screens.settings.SettingsScreen
import com.tonespace.app.ui.screens.upload.UploadScreen
import com.tonespace.app.ui.screens.wallet.WalletScreen

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

@Composable
fun ToneShareNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, "Home", Icons.Default.Home),
        BottomNavItem(Screen.Browse, "Browse", Icons.Default.Explore),
        BottomNavItem(Screen.Upload, "Upload", Icons.Default.Upload),
        BottomNavItem(Screen.Wallet, "Wallet", Icons.Default.Wallet),
        BottomNavItem(Screen.Profile, "Profile", Icons.Default.Person),
    )

    val showBottomBar = bottomNavItems.any { it.screen.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onSoundClick = { soundId -> navController.navigate(Screen.Player.createRoute(soundId)) },
                    onSearchClick = { navController.navigate(Screen.Search.route) }
                )
            }
            composable(Screen.Browse.route) {
                BrowseScreen(
                    onSoundClick = { soundId -> navController.navigate(Screen.Player.createRoute(soundId)) }
                )
            }
            composable(Screen.Upload.route) {
                UploadScreen()
            }
            composable(Screen.Wallet.route) {
                WalletScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLoginClick = { navController.navigate(Screen.Auth.route) },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onCreatorClick = { navController.navigate(Screen.Creator.route) }
                )
            }
            composable(
                route = Screen.Player.route,
                arguments = listOf(navArgument("soundId") { type = NavType.StringType })
            ) {
                PlayerScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.Auth.route) {
                AuthScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                    onSoundClick = { soundId -> navController.navigate(Screen.Player.createRoute(soundId)) }
                )
            }
            composable(Screen.Creator.route) {
                CreatorDashboardScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}