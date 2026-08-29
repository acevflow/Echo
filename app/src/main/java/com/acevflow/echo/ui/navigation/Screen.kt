package com.acevflow.echo.ui.navigation

sealed class Screen(val route: String) {
    data object Library : Screen("library")
    data object Player : Screen("player")
}
