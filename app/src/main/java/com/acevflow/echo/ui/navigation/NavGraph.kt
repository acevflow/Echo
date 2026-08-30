package com.acevflow.echo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.acevflow.echo.ui.details.AlbumDetailScreen
import com.acevflow.echo.ui.details.ArtistDetailScreen
import com.acevflow.echo.ui.details.PlaylistDetailScreen
import com.acevflow.echo.ui.library.LibraryScreen
import com.acevflow.echo.ui.library.albums.AlbumsScreen
import com.acevflow.echo.ui.library.artists.ArtistsScreen
import com.acevflow.echo.ui.library.recent.RecentScreen
import com.acevflow.echo.ui.playlists.PlaylistsScreen
import com.acevflow.echo.ui.settings.EqualizerScreen
import com.acevflow.echo.ui.settings.SettingsScreen
import com.acevflow.echo.ui.player.PlayerScreen
import com.acevflow.echo.ui.player.PlayerViewModel
import com.acevflow.echo.ui.search.SearchScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Songs.route,
        modifier = modifier
    ) {
        composable(Screen.Songs.route) {
            LibraryScreen(viewModel = hiltViewModel())
        }
        composable(Screen.Albums.route) {
            AlbumsScreen(
                viewModel = hiltViewModel(),
                onAlbumClick = { album ->
                    navController.navigate(Screen.AlbumDetail.createRoute(album.id))
                }
            )
        }
        composable(Screen.Artists.route) {
            ArtistsScreen(
                viewModel = hiltViewModel(),
                onArtistClick = { artist ->
                    navController.navigate(Screen.ArtistDetail.createRoute(artist.name))
                }
            )
        }
        composable(Screen.Playlists.route) {
            PlaylistsScreen(
                viewModel = hiltViewModel(),
                onPlaylistClick = { playlist ->
                    navController.navigate(Screen.PlaylistDetail.createRoute(playlist.id))
                }
            )
        }
        composable(Screen.Recent.route) {
            RecentScreen(viewModel = hiltViewModel())
        }
        composable(Screen.Search.route) {
            SearchScreen(
                viewModel = hiltViewModel(),
                onAlbumClick = { album ->
                    navController.navigate(Screen.AlbumDetail.createRoute(album.id))
                },
                onArtistClick = { artist ->
                    navController.navigate(Screen.ArtistDetail.createRoute(artist.name))
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = hiltViewModel(),
                onNavigateToEqualizer = { navController.navigate(Screen.Equalizer.route) }
            )
        }
        composable(Screen.Equalizer.route) {
            EqualizerScreen(viewModel = hiltViewModel())
        }
        composable(Screen.Player.route) {
            PlayerScreen(
                viewModel = hiltViewModel(),
                mainViewModel = hiltViewModel()
            )
        }
        composable(
            route = Screen.AlbumDetail.route,
            arguments = listOf(navArgument("albumId") { type = NavType.LongType })
        ) {
            AlbumDetailScreen(viewModel = hiltViewModel())
        }
        composable(
            route = Screen.ArtistDetail.route,
            arguments = listOf(navArgument("artistName") { type = NavType.StringType })
        ) {
            ArtistDetailScreen(
                viewModel = hiltViewModel(),
                onAlbumClick = { album ->
                    navController.navigate(Screen.AlbumDetail.createRoute(album.id))
                }
            )
        }
        composable(
            route = Screen.PlaylistDetail.route,
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) {
            PlaylistDetailScreen(viewModel = hiltViewModel())
        }
    }
}
