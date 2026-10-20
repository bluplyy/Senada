package com.senada.music.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senada.music.data.artwork.ArtworkColors
import com.senada.music.data.model.PlaybackState
import com.senada.music.data.model.Song
import com.senada.music.ui.components.FullPlaybackControls
import com.senada.music.ui.components.GlassSurface
import com.senada.music.ui.components.SenadaSeekBar
import com.senada.music.ui.components.SongArtwork
import com.senada.music.ui.theme.Ink
import com.senada.music.ui.theme.MetadataStyle
import com.senada.music.ui.theme.PlayerTitle

/**
 * Layar Pemutar Penuh (Frames 08 & 18):
 * Menggunakan gradasi dari warna dominan artwork lokal dengan transisi 240 ms,
 * panel kontrol liquid glass, seek bar yang tidak melompat saat di-drag,
 * dan adaptasi kontras otomatis di atas artwork terang maupun gelap.
 */
@Composable
fun FullPlayerScreen(
    song: Song,
    playbackState: PlaybackState,
    artworkBitmap: Bitmap?,
    artworkColors: ArtworkColors,
    onClose: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Animasi perubahan gradasi latar saat album berganti (240 ms sesuai handoff rU68D.3)
    val animatedTop by animateColorAsState(targetValue = artworkColors.topColor, animationSpec = tween(240), label = "topColor")
    val animatedMid by animateColorAsState(targetValue = artworkColors.middleColor, animationSpec = tween(240), label = "midColor")
    val animatedBottom by animateColorAsState(targetValue = artworkColors.bottomColor, animationSpec = tween(240), label = "bottomColor")

    val isDark = artworkColors.isDarkBackground
    val headerTextColor = if (isDark) Color(0xFFF1F5EC) else Color(0xFF3D453B)
    val titleTextColor = if (isDark) Color.White else Ink
    val artistTextColor = if (isDark) Color(0xFFF1F5EC) else Color(0xFF4D544A)
    val iconChevronColor = if (isDark) Color.White else Ink

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0.0f to animatedTop,
                    0.5f to animatedMid,
                    1.0f to animatedBottom
                )
            )
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Bar atas: Tombol tutup (chevron-down) dan judul "SEDANG DIPUTAR"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Tutup pemutar musik",
                        tint = iconChevronColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "SEDANG DIPUTAR",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = headerTextColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Foto album dari file aktif (maks 364x364 dp, radius 24 dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                SongArtwork(
                    song = song,
                    bitmap = artworkBitmap,
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 24.dp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Metadata aktif (Judul dan Penyanyi)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = song.title,
                    style = PlayerTitle.copy(color = titleTextColor),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.displayArtist,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = artistTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Panel kontrol liquid glass (reusable GlassSurface)
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SenadaSeekBar(
                        currentPositionMs = playbackState.currentPositionMs,
                        durationMs = playbackState.durationMs,
                        onSeek = onSeek
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FullPlaybackControls(
                        isPlaying = playbackState.isPlaying,
                        shuffleActive = playbackState.shuffleMode,
                        repeatMode = playbackState.repeatMode,
                        onTogglePlayPause = onTogglePlayPause,
                        onToggleShuffle = onToggleShuffle,
                        onCycleRepeat = onCycleRepeat,
                        onPrevious = onPrevious,
                        onNext = onNext
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status kontrol mode pemutaran di bawah panel kaca
            Text(
                text = playbackState.statusDescription,
                style = MetadataStyle.copy(color = Color(0xFF51594A)),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .semantics {
                        contentDescription = "Status playback: ${playbackState.statusDescription}"
                    }
            )
        }
    }
}
