package com.acevflow.echo.media.audio

import androidx.media3.common.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Manages volume automation for smooth fades.
 */
class VolumeController(private val scope: CoroutineScope) {
    private var fadeJob: Job? = null

    /**
     * Fades the volume from 0.0 to 1.0.
     */
    fun fadeIn(player: Player, durationMs: Long) {
        fadeJob?.cancel()
        fadeJob = scope.launch {
            val steps = 20
            val interval = (durationMs / steps).milliseconds
            for (i in 1..steps) {
                player.volume = i.toFloat() / steps
                delay(interval)
            }
            player.volume = 1.0f
        }
    }

    /**
     * Fades the volume from current to 0.0, then pauses.
     */
    fun fadeOutAndPause(player: Player, durationMs: Long) {
        fadeJob?.cancel()
        fadeJob = scope.launch {
            val startVolume = player.volume
            val steps = 20
            val interval = (durationMs / steps).milliseconds
            for (i in steps downTo 0) {
                player.volume = (i.toFloat() / steps) * startVolume
                delay(interval)
            }
            player.pause()
            player.volume = 1.0f // Reset for next play
        }
    }
}
