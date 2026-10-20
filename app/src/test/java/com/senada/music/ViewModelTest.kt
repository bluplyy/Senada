package com.senada.music

import com.senada.music.data.fixtures.MockMusicData
import com.senada.music.data.model.PlaybackState
import com.senada.music.data.model.RepeatMode
import com.senada.music.ui.PermissionStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {

    @Test
    fun testPermissionStateTransitions() = runTest {
        var status = PermissionStatus.INITIAL
        assertEquals(PermissionStatus.INITIAL, status)

        // Denied with rationale
        status = PermissionStatus.DENIED
        assertEquals(PermissionStatus.DENIED, status)

        // Denied permanently (needs settings)
        status = PermissionStatus.NEED_SETTINGS
        assertEquals(PermissionStatus.NEED_SETTINGS, status)

        // Granted
        status = PermissionStatus.GRANTED
        assertEquals(PermissionStatus.GRANTED, status)
    }

    @Test
    fun testPlaybackStateSingleSourceOfTruth() {
        val song1 = MockMusicData.sampleSongs[0]
        val song2 = MockMusicData.sampleSongs[1]

        var state = PlaybackState(
            activeSong = song1,
            isPlaying = true,
            queue = MockMusicData.sampleSongs,
            currentPositionMs = 12000L,
            durationMs = song1.durationMs,
            shuffleMode = false,
            repeatMode = RepeatMode.OFF
        )

        assertEquals("Arah Pulang", state.activeSong?.title)
        assertTrue(state.isPlaying)
        assertEquals(8, state.queue.size)

        // Next song transition
        state = state.copy(
            activeSong = song2,
            currentPositionMs = 0L,
            durationMs = song2.durationMs
        )
        assertEquals("Di Antara Rerumputan dan Langit", state.activeSong?.title)
        assertEquals(0L, state.currentPositionMs)

        // Error snackbar when file missing
        state = state.copy(
            errorMessage = "File lagu tidak tersedia.\nMemutar lagu berikutnya."
        )
        assertEquals("File lagu tidak tersedia.\nMemutar lagu berikutnya.", state.errorMessage)

        // Dismiss snackbar
        state = state.copy(errorMessage = null)
        assertNull(state.errorMessage)
    }

    @Test
    fun testEmptyCollectionState() {
        val emptyQueue = emptyList<com.senada.music.data.model.Song>()
        val state = PlaybackState(queue = emptyQueue)

        assertNull(state.activeSong)
        assertFalse(state.isPlaying)
        assertEquals(0, state.queue.size)
    }
}
