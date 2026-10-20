package com.senada.music.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senada.music.data.model.Song
import com.senada.music.ui.components.SenadaSearchField
import com.senada.music.ui.components.SongRow
import com.senada.music.ui.theme.BodyAndButton
import com.senada.music.ui.theme.MetadataStyle
import com.senada.music.ui.theme.Muted
import com.senada.music.ui.theme.Olive
import com.senada.music.ui.theme.ScreenTitle
import com.senada.music.ui.theme.SectionTitle

/**
 * Layar Pencarian (Frames 05, 06, 07, 14):
 * Menangani state sebelum mengetik (05), hasil ditemukan (06), tidak ditemukan (07),
 * dan keyboard aktif (14) dengan perbaruan instan saat mengetik.
 */
@Composable
fun SearchScreen(
    songs: List<Song>,
    activeSong: Song?,
    isPlaying: Boolean,
    artworkProvider: (Long) -> Bitmap?,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }

    val filteredSongs = if (query.isBlank()) {
        emptyList()
    } else {
        songs.filter { song ->
            song.title.contains(query, ignoreCase = true) ||
            song.artist.contains(query, ignoreCase = true)
        }.sortedBy { it.title.lowercase() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Cari",
            style = ScreenTitle
        )

        Spacer(modifier = Modifier.height(16.dp))

        SenadaSearchField(
            query = query,
            onQueryChange = { query = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            // Frame 05: Sebelum mengetik / kolom kosong
            query.isBlank() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Olive,
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Temukan musikmu",
                            style = SectionTitle
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Ketik judul lagu atau penyanyi.\nPencarian hanya di koleksi perangkat.",
                            style = BodyAndButton.copy(color = Muted, textAlign = TextAlign.Center),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Frame 07: Kueri terisi tetapi tidak ditemukan
            filteredSongs.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2DDD5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = Olive,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "“$query” tidak ditemukan",
                            style = SectionTitle,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Coba judul atau nama penyanyi lain.",
                            style = BodyAndButton.copy(color = Muted, textAlign = TextAlign.Center)
                        )
                    }
                }
            }

            // Frame 06 & 14: Hasil ditemukan
            else -> {
                Text(
                    text = "${filteredSongs.size} lagu ditemukan",
                    style = MetadataStyle,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 170.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = filteredSongs,
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
    }
}
