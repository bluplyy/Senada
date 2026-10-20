package com.senada.music.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.senada.music.data.model.Folder
import com.senada.music.data.model.Song
import com.senada.music.ui.components.SongRow
import com.senada.music.ui.theme.BodyAndButton
import com.senada.music.ui.theme.Ink
import com.senada.music.ui.theme.MetadataStyle
import com.senada.music.ui.theme.ScreenTitle

/**
 * Layar 04 · Isi folder (Music / Download / folder lokal lainnya):
 * Tombol kembali "Folder", judul nama folder, volume path & jumlah lagu, daftar lagu dengan durasi.
 */
@Composable
fun FolderDetailScreen(
    folder: Folder,
    activeSong: Song?,
    isPlaying: Boolean,
    artworkProvider: (Long) -> Bitmap?,
    onBack: () -> Unit,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val sortedSongs = folder.songs.sortedBy { it.title.lowercase() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Tombol kembali ke Folder
        Row(
            modifier = Modifier
                .height(48.dp)
                .clickable(onClick = onBack)
                .semantics {
                    contentDescription = "Kembali ke daftar folder"
                    role = Role.Button
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Ink,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Folder",
                style = BodyAndButton
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = folder.name,
            style = ScreenTitle
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${folder.volumePath} • ${folder.songCount} lagu",
            style = MetadataStyle
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 170.dp),
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
