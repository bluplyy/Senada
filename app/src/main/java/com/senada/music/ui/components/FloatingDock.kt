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
import androidx.compose.material.icons.outlined.Audiotrack
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
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

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

enum class NavTab(val title: String, val iconRes: Int) {
    HOME("Home", com.senada.music.R.drawable.ic_lucide_house),
    LAGU("Lagu", com.senada.music.R.drawable.ic_lucide_music2),
    FOLDER("Folder", com.senada.music.R.drawable.ic_lucide_folder),
    CARI("Cari", com.senada.music.R.drawable.ic_lucide_search)
}

/**
 * Mini Player liquid glass (w: 380dp, h: 72dp, radius 24dp):
 * Thumbnail 48dp (radius 12dp), teks judul 14sp semibold & artis 12sp, kontrol jeda/putar dan berikutnya 23dp.
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
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SongArtwork(
                song = song,
                bitmap = artworkBitmap,
                modifier = Modifier.size(48.dp),
                cornerRadius = 12.dp
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onOpenFullPlayer)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "Buka pemutar penuh. Sedang memutar ${song.title} oleh ${song.displayArtist}"
                        role = Role.Button
                    },
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                Text(
                    text = song.title,
                    style = SongTitleStyle.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Ink
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.displayArtist,
                    style = MetadataStyle.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Muted
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = onTogglePlayPause,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(
                        if (isPlaying) com.senada.music.R.drawable.ic_lucide_pause
                        else com.senada.music.R.drawable.ic_lucide_play
                    ),
                    contentDescription = if (isPlaying) "Jeda musik" else "Putar musik",
                    tint = Ink,
                    modifier = Modifier.size(23.dp)
                )
            }

            IconButton(
                onClick = onNext,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(com.senada.music.R.drawable.ic_lucide_skip_forward),
                    contentDescription = "Lagu berikutnya",
                    tint = Ink,
                    modifier = Modifier.size(23.dp)
                )
            }
        }
    }
}

/**
 * Navigasi bawah liquid glass (tinggi 80 dp, radius 24 dp, p: 8dp, gap: 4dp):
 * 4 menu: Home, Lagu, Folder, Cari.
 * Item: h: 64dp, radius 18dp.
 * Item aktif (Home) memiliki latar Pink #F6B0BB, semibold 11sp, icon 22dp, gap: 4dp.
 * Item non-aktif memiliki latar transparan, normal 11sp, icon 22dp, gap: 4dp.
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
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab.entries.forEach { tab ->
                val isSelected = tab == currentTab
                val itemModifier = if (isSelected) {
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Pink)
                } else {
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onTabSelected(tab) }
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
                        painter = androidx.compose.ui.res.painterResource(tab.iconRes),
                        contentDescription = null,
                        tint = Ink,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tab.title,
                        style = NavLabelStyle.copy(
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = Ink
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
