package com.senada.music.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.senada.music.data.model.RepeatMode
import com.senada.music.ui.theme.ActiveDot
import com.senada.music.ui.theme.ControlInactive
import com.senada.music.ui.theme.Ink
import com.senada.music.ui.theme.Olive
import com.senada.music.ui.theme.Pink

/**
 * Tombol putar/jeda utama (64x64 dp, radius 32 dp, warna Olive #7E8C69, icon putih 28 dp).
 */
@Composable
fun MainPlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = if (isPlaying) "Jeda" else "Putar"

    Box(
        modifier = modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Olive)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = description
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

/**
 * Tombol acak (Shuffle):
 * 48x48 dp, nonaktif berlatar #E6E0D6, aktif berlatar Pink #F6B0BB + penanda titik aktif 4x4 dp #30382A.
 */
@Composable
fun ShuffleButton(
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = if (isActive) "Acak aktif, ketuk untuk menonaktifkan" else "Acak nonaktif, ketuk untuk mengaktifkan"

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isActive) Pink else ControlInactive)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = description
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = null,
                tint = Ink,
                modifier = Modifier.size(22.dp)
            )
            if (isActive) {
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(ActiveDot)
                )
            }
        }
    }
}

/**
 * Tombol ulang (Repeat):
 * Mode siklus: OFF -> ALL -> ONE.
 * OFF: latar #E6E0D6, icon repeat
 * ALL: latar Pink #F6B0BB, icon repeat + penanda aktif
 * ONE: latar Pink #F6B0BB, icon repeat-1 + penanda aktif
 */
@Composable
fun RepeatButton(
    repeatMode: RepeatMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = when (repeatMode) {
        RepeatMode.OFF -> "Ulang nonaktif, ketuk untuk ulang semua"
        RepeatMode.ALL -> "Ulang semua lagu aktif, ketuk untuk ulang satu"
        RepeatMode.ONE -> "Ulang satu lagu aktif, ketuk untuk menonaktifkan"
    }

    val isActive = repeatMode != RepeatMode.OFF

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isActive) Pink else ControlInactive)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = description
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                contentDescription = null,
                tint = Ink,
                modifier = Modifier.size(22.dp)
            )
            if (isActive) {
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(ActiveDot)
                )
            }
        }
    }
}

/**
 * Panel kontrol playback lengkap untuk pemutar penuh:
 * Shuffle - Prev - Play/Pause (64dp) - Next - Repeat.
 */
@Composable
fun FullPlaybackControls(
    isPlaying: Boolean,
    shuffleActive: Boolean,
    repeatMode: RepeatMode,
    onTogglePlayPause: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShuffleButton(
            isActive = shuffleActive,
            onClick = onToggleShuffle
        )

        IconButton(
            onClick = onPrevious,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Lagu sebelumnya",
                tint = Ink,
                modifier = Modifier.size(26.dp)
            )
        }

        MainPlayPauseButton(
            isPlaying = isPlaying,
            onClick = onTogglePlayPause
        )

        IconButton(
            onClick = onNext,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Lagu berikutnya",
                tint = Ink,
                modifier = Modifier.size(26.dp)
            )
        }

        RepeatButton(
            repeatMode = repeatMode,
            onClick = onCycleRepeat
        )
    }
}
