package com.senada.music.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.senada.music.data.model.Song
import com.senada.music.ui.components.SongRow
import com.senada.music.ui.theme.MetadataStyle
import com.senada.music.ui.theme.ScreenTitle

/**
 * Layar 02 · Semua lagu:
 * Menampilkan seluruh lagu lokal perangkat terurut judul A–Z dengan durasi di kanan.
 */
@Composable
fun SongsScreen(
    songs: List<Song>,
    activeSong: Song?,
    isPlaying: Boolean,
    artworkProvider: (Long) -> Bitmap?,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    // Urutan judul A-Z sesuai spesifikasi handoff
    val sortedSongs = songs.sortedBy { it.title.lowercase() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lagu",
            style = ScreenTitle
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${songs.size} lagu • Judul A–Z",
            style = MetadataStyle
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 200.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = sortedSongs,
                key = { it.id }
            ) { song ->
                SongRow(
                    song = song,
                    isActive = song.id == activeSong?.id,
                    isPlaying = isPlaying && (song.id == activeSong?.id),
                    artworkBitmap = artworkProvider(song.id),
                    onClick = { onSongClick(song) },
                    showDuration = true
                )
            }
        }
    }
}
