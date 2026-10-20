package com.senada.music.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senada.music.data.model.Folder
import com.senada.music.data.model.Song
import com.senada.music.ui.theme.Action
import com.senada.music.ui.theme.BgBottom
import com.senada.music.ui.theme.BgTop
import com.senada.music.ui.theme.BodyAndButton
import com.senada.music.ui.theme.Cream
import com.senada.music.ui.theme.GlassBorder
import com.senada.music.ui.theme.GlassFill
import com.senada.music.ui.theme.GlassShadow
import com.senada.music.ui.theme.Ink
import com.senada.music.ui.theme.MetadataStyle
import com.senada.music.ui.theme.Muted
import com.senada.music.ui.theme.Olive
import com.senada.music.ui.theme.PinkSoft
import com.senada.music.ui.theme.Sage
import com.senada.music.ui.theme.SearchFieldBorder
import com.senada.music.ui.theme.SearchFieldFill
import com.senada.music.ui.theme.SnackbarBg
import com.senada.music.ui.theme.SongTitleStyle

/**
 * Background gradasi vertikal 180° aplikasi Senada:
 * 0% #F3ECE6 -> 55% #ECE0DA -> 100% #D8DECE
 */
@Composable
fun SenadaBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0.0f to BgTop,
                    0.55f to Cream,
                    1.0f to BgBottom
                )
            )
    ) {
        content()
    }
}

/**
 * Surface liquid glass dengan border putih 70%, bayangan halus, dan fallback semi-transparan.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    contentColor: Color = Ink,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = shape, spotColor = GlassShadow, ambientColor = GlassShadow)
            .clip(shape)
            .background(GlassFill)
            .border(width = 1.dp, color = GlassBorder, shape = shape)
    ) {
        content()
    }
}

/**
 * Fallback artwork gradasi 135° pink-soft ke sage dengan ikon musik note ink.
 */
@Composable
fun FallbackArtwork(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    0.0f to PinkSoft,
                    1.0f to Sage
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Artwork bawaan",
            tint = Ink,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Artwork lokal lagu: menampilkan gambar album jika tersedia, atau gradasi fallback.
 */
@Composable
fun SongArtwork(
    bitmap: Bitmap?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp
) {
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Sampul album",
            modifier = modifier.clip(RoundedCornerShape(cornerRadius)),
            contentScale = ContentScale.Crop
        )
    } else {
        FallbackArtwork(modifier = modifier, cornerRadius = cornerRadius)
    }
}

/**
 * Baris lagu (Song row):
 * Tinggi 80 dp, thumbnail 56 dp (radius 12 dp), judul (max 2 baris), penyanyi, durasi di kanan.
 * Lagu aktif ditandai perubahan warna judul ke Olive (#596749) dan simbol jeda '||'.
 */
@Composable
fun SongRow(
    song: Song,
    isActive: Boolean,
    isPlaying: Boolean,
    artworkBitmap: Bitmap?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDuration: Boolean = true
) {
    val activeTitleColor = Olive
    val talkBackDesc = buildString {
        append(song.title)
        append(", oleh ")
        append(song.displayArtist)
        if (showDuration) {
            append(", durasi ")
            append(song.formattedDuration())
        }
        if (isActive) {
            append(if (isPlaying) ", sedang diputar" else ", dijeda")
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = talkBackDesc
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SongArtwork(
            bitmap = artworkBitmap,
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = song.title,
                style = SongTitleStyle.copy(
                    color = if (isActive) activeTitleColor else Ink
                ),
                maxLines = 2,
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

        if (showDuration) {
            Spacer(modifier = Modifier.width(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isActive) {
                    Text(
                        text = if (isPlaying) "|| " else "▶ ",
                        style = MetadataStyle.copy(color = Olive, fontWeight = FontWeight.Bold)
                    )
                }
                Text(
                    text = song.formattedDuration(),
                    style = MetadataStyle
                )
            }
        }
    }
}

/**
 * Baris folder (Folder row):
 * Tinggi 88 dp, ikon folder di dalam container abu-abu lembut, nama folder, jumlah lagu, chevron-right.
 */
@Composable
fun FolderRow(
    folder: Folder,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val talkBackDesc = "${folder.name}, ${folder.songCount} lagu"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 12.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = talkBackDesc
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE2DDD5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = Ink,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = folder.name,
                style = SongTitleStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${folder.songCount} lagu",
                style = MetadataStyle
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Muted,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Kolom pencarian (Search field):
 * Tinggi 56 dp, radius 20 dp, search icon, tombol 'X' untuk hapus saat terisi.
 */
@Composable
fun SenadaSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Cari judul lagu atau penyanyi"
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SearchFieldFill)
            .border(1.dp, SearchFieldBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Cari",
            tint = Ink,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    style = BodyAndButton.copy(color = Muted)
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = BodyAndButton.copy(color = Ink),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Hapus kueri pencarian",
                    tint = Ink,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Seek bar interaktif:
 * Menangani drag tanpa thumb melompat sebelum dilepas (smooth dragging).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SenadaSeekBar(
    currentPositionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragPositionMs by remember { mutableFloatStateOf(0f) }

    val safeDuration = if (durationMs > 0) durationMs.toFloat() else 1f
    val displayPosition = if (isDragging) dragPositionMs else currentPositionMs.toFloat().coerceIn(0f, safeDuration)

    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = displayPosition,
            onValueChange = { newPos ->
                isDragging = true
                dragPositionMs = newPos
            },
            onValueChangeFinished = {
                onSeek(dragPositionMs.toLong())
                isDragging = false
            },
            valueRange = 0f..safeDuration,
            colors = SliderDefaults.colors(
                thumbColor = Ink,
                activeTrackColor = Olive,
                inactiveTrackColor = Color(0x33292E28)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(displayPosition.toLong()),
                style = MetadataStyle
            )
            Text(
                text = formatDuration(durationMs),
                style = MetadataStyle
            )
        }
    }
}

private fun formatDuration(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(java.util.Locale.getDefault(), "%d:%02d", minutes, seconds)
}

/**
 * Snackbar file tidak tersedia:
 * Muncul di atas mini player, background #30372D, cornerRadius 16dp.
 */
@Composable
fun MissingFileSnackbar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onDismiss),
        color = SnackbarBg,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = BodyAndButton.copy(color = Color.White, fontSize = 13.sp)
            )
        }
    }
}
