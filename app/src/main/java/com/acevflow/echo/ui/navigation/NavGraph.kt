package com.acevflow.echo.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.acevflow.echo.ui.details.AlbumDetailScreen
import com.acevflow.echo.ui.details.ArtistDetailScreen
import com.acevflow.echo.ui.details.FolderDetailScreen
import com.acevflow.echo.ui.details.GenreDetailScreen
import com.acevflow.echo.ui.details.PlaylistDetailScreen
import com.acevflow.echo.ui.library.LibraryScreen
import com.acevflow.echo.ui.library.albums.AlbumsScreen
import com.acevflow.echo.ui.library.artists.ArtistsScreen
import com.acevflow.echo.ui.library.genres.GenresScreen
import com.acevflow.echo.ui.library.folders.FoldersScreen
import com.acevflow.echo.ui.library.recent.RecentScreen
import com.acevflow.echo.ui.playlists.PlaylistsScreen
import com.acevflow.echo.ui.editor.EditSongScreen
import com.acevflow.echo.ui.settings.EqualizerScreen
import com.acevflow.echo.ui.settings.ExcludedFoldersScreen
import com.acevflow.echo.ui.settings.SettingsScreen
import com.acevflow.echo.ui.player.PlayerScreen
import com.acevflow.echo.ui.player.PlayerViewModel
import com.acevflow.echo.ui.search.SearchScreen

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalNavAnimatedVisibilityScope = compositionLocalOf<androidx.compose.animation.AnimatedVisibilityScope?> { null }

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
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                LibraryScreen(
                    viewModel = hiltViewModel(),
                    onNavigateToEdit = { songId ->
                        navController.navigate(Screen.EditSong.createRoute(songId))
                    },
                    onNavigateToAlbums = { navController.navigate(Screen.Albums.route) },
                    onNavigateToArtists = { navController.navigate(Screen.Artists.route) },
                    onNavigateToGenres = { navController.navigate(Screen.Genres.route) },
                    onNavigateToFolders = { navController.navigate(Screen.Folders.route) },
                    onNavigateToRecent = { navController.navigate(Screen.Recent.route) }
                )
            }
        }
        composable(Screen.Albums.route) {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                AlbumsScreen(
                    viewModel = hiltViewModel(),
                    onAlbumClick = { album ->
                        navController.navigate(Screen.AlbumDetail.createRoute(album.id))
                    }
                )
            }
        }
        composable(Screen.Artists.route) {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                ArtistsScreen(
                    viewModel = hiltViewModel(),
                    onArtistClick = { artist ->
                        navController.navigate(Screen.ArtistDetail.createRoute(artist.name))
                    }
                )
            }
        }
        composable(Screen.Genres.route) {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                GenresScreen(
                    viewModel = hiltViewModel(),
                    onGenreClick = { genre ->
                        navController.navigate(Screen.GenreDetail.createRoute(genre.id))
                    }
                )
            }
        }
        composable(Screen.Folders.route) {
            FoldersScreen(
                viewModel = hiltViewModel(),
                onFolderClick = { folder ->
                    navController.navigate(Screen.FolderDetail.createRoute(folder.path))
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
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                RecentScreen(
                    viewModel = hiltViewModel(),
                    onNavigateToEdit = { songId ->
                        navController.navigate(Screen.EditSong.createRoute(songId))
                    }
                )
            }
        }
        composable(Screen.Search.route) {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                SearchScreen(
                    viewModel = hiltViewModel(),
                    onAlbumClick = { album ->
                        navController.navigate(Screen.AlbumDetail.createRoute(album.id))
                    },
                    onArtistClick = { artist ->
                        navController.navigate(Screen.ArtistDetail.createRoute(artist.name))
                    },
                    onNavigateToEdit = { songId ->
                        navController.navigate(Screen.EditSong.createRoute(songId))
                    }
                )
            }
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = hiltViewModel(),
                onNavigateToEqualizer = { navController.navigate(Screen.Equalizer.route) },
                onNavigateToExcludedFolders = { navController.navigate(Screen.ExcludedFolders.route) }
            )
        }
        composable(Screen.ExcludedFolders.route) {
            ExcludedFoldersScreen(viewModel = hiltViewModel())
        }
        composable(Screen.Equalizer.route) {
            EqualizerScreen(viewModel = hiltViewModel())
        }
        composable(
            route = Screen.EditSong.route,
            arguments = listOf(navArgument("songId") { type = NavType.LongType })
        ) {
            EditSongScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Player.route) {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                PlayerScreen(
                    viewModel = hiltViewModel(),
                    mainViewModel = hiltViewModel(),
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable(
            route = Screen.AlbumDetail.route,
            arguments = listOf(navArgument("albumId") { type = NavType.LongType })
        ) {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                AlbumDetailScreen(
                    viewModel = hiltViewModel(),
                    onNavigateToEdit = { songId ->
                        navController.navigate(Screen.EditSong.createRoute(songId))
                    }
                )
            }
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
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                PlaylistDetailScreen(
                    viewModel = hiltViewModel(),
                    onNavigateToEdit = { songId ->
                        navController.navigate(Screen.EditSong.createRoute(songId))
                    }
                )
            }
        }
        composable(
            route = Screen.FolderDetail.route,
            arguments = listOf(navArgument("folderPath") { type = NavType.StringType })
        ) {
            CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                FolderDetailScreen(
                    viewModel = hiltViewModel(),
                    onNavigateToEdit = { songId ->
                        navController.navigate(Screen.EditSong.createRoute(songId))
                    }
                )
            }
        }
        composable(
            route = Screen.GenreDetail.route,
            arguments = listOf(navArgument("genreId") { type = NavType.LongType })
        ) {
            GenreDetailScreen(
                viewModel = hiltViewModel(),
                onNavigateToEdit = { songId ->
                    navController.navigate(Screen.EditSong.createRoute(songId))
                }
            )
        }
    }
}
