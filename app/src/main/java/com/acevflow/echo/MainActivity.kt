package com.acevflow.echo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.acevflow.echo.ui.MainViewModel
import com.acevflow.echo.ui.components.MiniPlayer
import com.acevflow.echo.ui.navigation.NavGraph
import com.acevflow.echo.ui.navigation.Screen
import com.acevflow.echo.ui.theme.EchoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val themeMode by mainViewModel.themeMode.collectAsState(initial = 0)
            val dynamicColorEnabled by mainViewModel.dynamicColorEnabled.collectAsState(initial = true)
            
            val darkTheme = when (themeMode) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            EchoTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColorEnabled
            ) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                val currentMediaItem by mainViewModel.currentMediaItem.collectAsState()

                val items = listOf(
                    Triple(Screen.Songs, "Songs", Icons.Default.MusicNote),
                    Triple(Screen.Albums, "Albums", Icons.Default.Album),
                    Triple(Screen.Artists, "Artists", Icons.Default.Person),
                    Triple(Screen.Folders, "Folders", Icons.Default.Folder),
                    Triple(Screen.Playlists, "Playlists", Icons.AutoMirrored.Filled.PlaylistPlay),
                    Triple(Screen.Recent, "Recent", Icons.Default.Restore)
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        val showTopBar = items.any { it.first.route == currentDestination?.route }
                        if (showTopBar) {
                            TopAppBar(
                                title = { 
                                    Text(
                                        text = "Echo",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleLarge
                                    ) 
                                },
                                actions = {
                                    IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                                        Icon(Icons.Default.Search, contentDescription = "Search")
                                    }
                                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    titleContentColor = MaterialTheme.colorScheme.onBackground
                                )
                            )
                        }
                    },
                    bottomBar = {
                        Column {
                            AnimatedVisibility(
                                visible = currentMediaItem != null && 
                                        currentDestination?.route != Screen.Player.route && 
                                        currentDestination?.route != Screen.Search.route,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                MiniPlayer(
                                    viewModel = mainViewModel,
                                    onClick = {
                                        navController.navigate(Screen.Player.route)
                                    }
                                )
                            }
                            
                            val showNavBar = items.any { it.first.route == currentDestination?.route }
                            if (showNavBar) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    tonalElevation = 0.dp
                                ) {
                                    items.forEach { (screen, label, icon) ->
                                        NavigationBarItem(
                                            icon = { Icon(icon, contentDescription = null) },
                                            label = { Text(label) },
                                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
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
                        }
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
