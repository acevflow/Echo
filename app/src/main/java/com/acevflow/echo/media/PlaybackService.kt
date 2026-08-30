package com.acevflow.echo.media

import android.content.Intent
import android.media.audiofx.Equalizer
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.data.preferences.UserPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A [MediaSessionService] that manages the lifetime of an [ExoPlayer] and its [MediaSession].
 * It handles background playback and provides a consistent interface for the Android system.
 */
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var musicRepository: MusicRepository

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    private var mediaSession: MediaSession? = null
    private var equalizer: Equalizer? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            mediaItem?.mediaId?.toLongOrNull()?.let { songId ->
                serviceScope.launch {
                    musicRepository.addSongToHistory(songId)
                }
            }
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .build()
        
        player.addListener(playerListener)
        
        applyEqualizer(player.audioSessionId)

        mediaSession = MediaSession.Builder(this, player).build()
    }

    private fun applyEqualizer(audioSessionId: Int) {
        if (audioSessionId == -1) return
        
        equalizer?.release()
        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                serviceScope.launch {
                    preferencesRepository.equalizerEnabled.collect { isEnabled ->
                        enabled = isEnabled
                    }
                }
                serviceScope.launch {
                    preferencesRepository.equalizerBands.collect { bands ->
                        bands.forEach { (band, gain) ->
                            if (band < numberOfBands) {
                                setBandLevel(band.toShort(), gain.toShort())
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player != null) {
            if (!player.playWhenReady || player.mediaItemCount == 0 || player.playbackState == Player.STATE_ENDED) {
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        equalizer?.release()
        equalizer = null
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
