package com.acevflow.echo.ui.navigation

sealed class Screen(val route: String) {
    data object Songs : Screen("songs")
    data object Albums : Screen("albums")
    data object Artists : Screen("artists")
    data object Search : Screen("search")
    data object Player : Screen("player")
    data object AlbumDetail : Screen("album_detail/{albumId}") {
        fun createRoute(albumId: Long) = "album_detail/$albumId"
    }
    data object ArtistDetail : Screen("artist_detail/{artistName}") {
        fun createRoute(artistName: String) = "artist_detail/$artistName"
    }
}
