package com.senada.music.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senada.music.data.model.Song
import com.senada.music.ui.components.SongRow
import com.senada.music.ui.theme.Ink
import com.senada.music.ui.theme.MetadataStyle
import com.senada.music.ui.theme.SectionTitle

/**
 * Layar 01 · Home:
 * Menampilkan koleksi terbaru ditambahkan (DATE_ADDED menurun),
 * header "senada", subtitle jumlah lagu, dan daftar lagu.
 */
@Composable
fun HomeScreen(
    songs: List<Song>,
    activeSong: Song?,
    isPlaying: Boolean,
    artworkProvider: (Long) -> Bitmap?,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    // Urutan DATE_ADDED menurun sesuai spesifikasi rU68D.3
    val recentSongs = songs.sortedByDescending { it.dateAdded }

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Branding header logo "senada" (dari aset Group 3.png)
        Image(
            painter = painterResource(com.senada.music.R.drawable.ic_logo_senada),
            contentDescription = "senada",
            modifier = Modifier.height(28.dp),
            contentScale = ContentScale.Fit,
            alignment = Alignment.CenterStart
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Koleksi lokal • ${songs.size} lagu di perangkat",
            style = MetadataStyle
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Baru ditambahkan",
                style = SectionTitle
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = "Urutan tanggal ditambahkan terbaru",
                tint = Ink,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 200.dp), // Ruang agar tidak tertutup floating dock
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = recentSongs,
                key = { it.id }
            ) { song ->
                SongRow(
                    song = song,
                    isActive = song.id == activeSong?.id,
                    isPlaying = isPlaying && (song.id == activeSong?.id),
                    artworkBitmap = artworkProvider(song.id),
                    onClick = { onSongClick(song) },
                    showDuration = false // Pada frame Home, durasi tidak ditampilkan
                )
            }
        }
    }
}
