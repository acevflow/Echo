package com.acevflow.echo.media

import android.content.Intent
import android.media.audiofx.DynamicsProcessing
import android.media.audiofx.Equalizer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.acevflow.echo.data.repository.MusicRepository
import com.acevflow.echo.data.preferences.UserPreferencesRepository
import com.acevflow.echo.media.audio.VolumeController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
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
    private var dynamicsProcessing: DynamicsProcessing? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var volumeController: VolumeController
    private var crossfadeJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            mediaItem?.mediaId?.toLongOrNull()?.let { songId ->
                serviceScope.launch {
                    musicRepository.addSongToHistory(songId)
                }
            }
            
            // Start fading in on every new item
            mediaSession?.player?.let {
                volumeController.fadeIn(it, 500)
            }
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        
        volumeController = VolumeController(serviceScope)
        
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(30000, 60000, 2500, 5000)
            .setBackBuffer(5000, true)
            .build()

        val player = ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            applyDynamicsProcessing(player.audioSessionId)
        }

        mediaSession = MediaSession.Builder(this, player).build()
        
        startCrossfadeMonitor(player)
    }

    private fun startCrossfadeMonitor(player: Player) {
        crossfadeJob?.cancel()
        crossfadeJob = serviceScope.launch {
            while (isActive) {
                delay(500.milliseconds)
                val duration = player.duration
                val position = player.currentPosition
                val crossfadeSeconds = preferencesRepository.crossfadeDuration.first()
                
                if (crossfadeSeconds > 0 && duration > 0 && (duration - position) <= (crossfadeSeconds * 1000L)) {
                    if (player.hasNextMediaItem()) {
                        volumeController.fadeOutAndPause(player, (crossfadeSeconds * 1000L))
                        delay(crossfadeSeconds.toLong() * 1000L)
                        player.seekToNext()
                        player.play()
                    }
                }
            }
        }
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

    @RequiresApi(Build.VERSION_CODES.P)
    private fun applyDynamicsProcessing(audioSessionId: Int) {
        if (audioSessionId == -1 || Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return

        dynamicsProcessing?.release()
        try {
            // A simple Limiter/Loudness configuration for normalization
            val config = DynamicsProcessing.Config.Builder(
                0, // variant
                1, // channel count
                true, // preEq
                0, // preEq band count
                true, // mbc
                0, // mbc band count
                true, // postEq
                0, // postEq band count
                true // limiter
            ).build()

            dynamicsProcessing = DynamicsProcessing(0, audioSessionId, config).apply {
                serviceScope.launch {
                    preferencesRepository.normalizationEnabled.collect { isEnabled ->
                        enabled = isEnabled
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
        dynamicsProcessing?.release()
        dynamicsProcessing = null
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
