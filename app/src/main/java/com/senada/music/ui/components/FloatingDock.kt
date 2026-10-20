package com.senada.music.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.senada.music.data.model.Song
import com.senada.music.ui.theme.Ink
import com.senada.music.ui.theme.MetadataStyle
import com.senada.music.ui.theme.Muted
import com.senada.music.ui.theme.NavLabelStyle
import com.senada.music.ui.theme.Pink
import com.senada.music.ui.theme.SongTitleStyle

enum class NavTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    LAGU("Lagu", Icons.Default.MusicNote),
    FOLDER("Folder", Icons.Default.Folder),
    CARI("Cari", Icons.Default.Search)
}

/**
 * Mini Player liquid glass (tinggi 72 dp, radius 24 dp):
 * Thumbnail 48 dp, teks judul & artis, kontrol jeda/putar dan berikutnya.
 * Ketuk metadata membuka pemutar penuh tanpa menghentikan musik.
 */
@Composable
fun MiniPlayer(
    song: Song,
    isPlaying: Boolean,
    artworkBitmap: Bitmap?,
    onOpenFullPlayer: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onOpenFullPlayer),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SongArtwork(
                bitmap = artworkBitmap,
                modifier = Modifier.size(48.dp),
                cornerRadius = 12.dp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onOpenFullPlayer)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "Buka pemutar penuh. Sedang memutar ${song.title} oleh ${song.displayArtist}"
                        role = Role.Button
                    },
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.title,
                    style = SongTitleStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = song.displayArtist,
                    style = MetadataStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onTogglePlayPause,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Jeda musik" else "Putar musik",
                    tint = Ink,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = onNext,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Lagu berikutnya",
                    tint = Ink,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Navigasi bawah liquid glass (tinggi 80 dp, radius 24 dp):
 * 4 menu: Home, Lagu, Folder, Cari.
 * Item aktif memiliki kapsul latar Pink #F6B0BB.
 */
@Composable
fun BottomNavigationDock(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab.entries.forEach { tab ->
                val isSelected = tab == currentTab
                val itemModifier = if (isSelected) {
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Pink)
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                } else {
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                }

                Column(
                    modifier = itemModifier.semantics {
                        selected = isSelected
                        role = Role.Tab
                        contentDescription = "Menu ${tab.title}${if (isSelected) ", aktif" else ""}"
                    },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = null,
                        tint = if (isSelected) Ink else Muted,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.title,
                        style = NavLabelStyle.copy(
                            color = if (isSelected) Ink else Muted
                        )
                    )
                }
            }
        }
    }
}

/**
 * Floating dock utama:
 * Berisi Mini Player (jika lagu aktif) + Bottom Navigation.
 * Menyembunyikan dock secara otomatis saat software keyboard aktif (sesuai handoff frame 14 & rU68D.2).
 */
@Composable
fun FloatingDock(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    activeSong: Song?,
    isPlaying: Boolean,
    artworkBitmap: Bitmap?,
    onOpenFullPlayer: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime
    val isKeyboardVisible = imeInsets.getBottom(density) > 0

    AnimatedVisibility(
        visible = !isKeyboardVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (activeSong != null) {
                MiniPlayer(
                    song = activeSong,
                    isPlaying = isPlaying,
                    artworkBitmap = artworkBitmap,
                    onOpenFullPlayer = onOpenFullPlayer,
                    onTogglePlayPause = onTogglePlayPause,
                    onNext = onNext
                )
            }

            BottomNavigationDock(
                currentTab = currentTab,
                onTabSelected = onTabSelected
            )
        }
    }
}
