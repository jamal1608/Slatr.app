package com.tonespace.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Browse : Screen("browse")
    object Upload : Screen("upload")
    object Creator : Screen("creator")
    object Wallet : Screen("wallet")
    object Profile : Screen("profile")
    object Search : Screen("search")
    object Auth : Screen("auth")
    object Settings : Screen("settings")
    object Player : Screen("player/{soundId}") {
        fun createRoute(soundId: String) = "player/$soundId"
    }
}