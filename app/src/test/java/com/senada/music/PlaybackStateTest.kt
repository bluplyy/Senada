package com.senada.music

import com.senada.music.data.fixtures.MockMusicData
import com.senada.music.data.model.PlaybackState
import com.senada.music.data.model.RepeatMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackStateTest {

    @Test
    fun testRepeatModeCycle() {
        var mode = RepeatMode.OFF
        mode = mode.next()
        assertEquals(RepeatMode.ALL, mode)
        mode = mode.next()
        assertEquals(RepeatMode.ONE, mode)
        mode = mode.next()
        assertEquals(RepeatMode.OFF, mode)
    }

    @Test
    fun testFormattedDurationAndPosition() {
        val song = MockMusicData.sampleSongs.first { it.title == "Senja di Jendela" }
        assertEquals("4:12", song.formattedDuration())

        val state = PlaybackState(
            activeSong = song,
            isPlaying = true,
            currentPositionMs = (1 * 60 + 38) * 1000L, // 1:38
            durationMs = (4 * 60 + 12) * 1000L,        // 4:12
            shuffleMode = true,
            repeatMode = RepeatMode.OFF
        )

        assertEquals("1:38", state.formattedPosition)
        assertEquals("4:12", state.formattedDuration)
        assertEquals("Acak aktif · Ulang nonaktif", state.statusDescription)
    }

    @Test
    fun testPausedAndRepeatOneStatusDescription() {
        val song = MockMusicData.sampleSongs.first { it.title == "Ruang Teduh" }
        assertEquals("5:01", song.formattedDuration())

        val state = PlaybackState(
            activeSong = song,
            isPlaying = false,
            currentPositionMs = 0L,
            durationMs = (5 * 60 + 1) * 1000L,
            shuffleMode = false,
            repeatMode = RepeatMode.ONE
        )

        assertEquals("0:00", state.formattedPosition)
        assertEquals("5:01", state.formattedDuration)
        assertEquals("Acak nonaktif · Ulang satu", state.statusDescription)
    }

    @Test
    fun testUnknownArtistFallback() {
        val song = MockMusicData.sampleSongs.first { it.title == "Langkah Kecil" }
        assertEquals("Penyanyi tidak diketahui", song.displayArtist)
        assertEquals("2:54", song.formattedDuration())
    }
}
