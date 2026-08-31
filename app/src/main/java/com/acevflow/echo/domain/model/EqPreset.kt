package com.acevflow.echo.domain.model

/**
 * Data class representing a predefined Equalizer curve.
 * Bands correspond to: 60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz.
 */
data class EqPreset(
    val name: String,
    val gains: List<Int>
) {
    companion object {
        val ALL = listOf(
            EqPreset("Flat", listOf(0, 0, 0, 0, 0)),
            EqPreset("Rock", listOf(5, 3, -1, 3, 5)),
            EqPreset("Jazz", listOf(4, 2, 0, 2, 4)),
            EqPreset("Pop", listOf(-1, 2, 5, 2, -1)),
            EqPreset("Bass Boost", listOf(6, 4, 0, 0, 0)),
            EqPreset("Vocal", listOf(0, 0, 4, 4, 0)),
            EqPreset("Classical", listOf(5, 3, 0, 4, 4)),
            EqPreset("Dance", listOf(6, 0, 2, 4, 1))
        )
    }
}
