package com.acevflow.echo.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.acevflow.echo.MainActivity
import com.acevflow.echo.R
import com.acevflow.echo.domain.model.Playlist
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository responsible for managing dynamic launcher shortcuts.
 */
@Singleton
class ShortcutRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Updates dynamic shortcuts based on the provided playlists.
     */
    fun updateDynamicShortcuts(playlists: List<Playlist>) {
        val shortcuts = playlists.take(3).map { playlist ->
            ShortcutInfoCompat.Builder(context, "playlist_${playlist.id}")
                .setShortLabel(playlist.name)
                .setLongLabel("Play ${playlist.name}")
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground))
                .setIntent(
                    Intent(context, MainActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        putExtra("playlist_id", playlist.id)
                    }
                )
                .build()
        }
        ShortcutManagerCompat.addDynamicShortcuts(context, shortcuts)
    }
}
