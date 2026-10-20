package com.senada.music.ui.previews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.senada.music.data.artwork.ArtworkColors
import com.senada.music.data.fixtures.MockMusicData
import com.senada.music.data.model.PlaybackState
import com.senada.music.data.model.RepeatMode
import com.senada.music.ui.components.FloatingDock
import com.senada.music.ui.components.MissingFileSnackbar
import com.senada.music.ui.components.NavTab
import com.senada.music.ui.components.SenadaBackground
import com.senada.music.ui.screens.EmptyCollectionScreen
import com.senada.music.ui.screens.FolderDetailScreen
import com.senada.music.ui.screens.FoldersScreen
import com.senada.music.ui.screens.FullPlayerScreen
import com.senada.music.ui.screens.HomeScreen
import com.senada.music.ui.screens.PermissionDeniedScreen
import com.senada.music.ui.screens.PermissionRequestScreen
import com.senada.music.ui.screens.ScanningScreen
import com.senada.music.ui.screens.SearchScreen
import com.senada.music.ui.screens.SongsScreen
import com.senada.music.ui.theme.SenadaTheme

@Preview(name = "01 · Home Screen (412x892)", widthDp = 412, heightDp = 892, showBackground = true)
@Composable
fun Preview01HomeScreen() {
    val songs = MockMusicData.sampleSongs
    val activeSong = songs.first { it.title == "Senja di Jendela" }

    SenadaTheme {
        SenadaBackground {
            Box(modifier = Modifier.fillMaxSize()) {
                HomeScreen(
                    songs = songs,
                    activeSong = activeSong,
                    isPlaying = true,
                    artworkProvider = { null },
                    onSongClick = {}
                )
                FloatingDock(
                    currentTab = NavTab.HOME,
                    onTabSelected = {},
                    activeSong = activeSong,
                    isPlaying = true,
                    artworkBitmap = null,
                    onOpenFullPlayer = {},
                    onTogglePlayPause = {},
                    onNext = {},
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview(name = "02 · Semua Lagu (412x892)", widthDp = 412, heightDp = 892, showBackground = true)
@Composable
fun Preview02SongsScreen() {
    val songs = MockMusicData.sampleSongs
    val activeSong = songs.first { it.title == "Senja di Jendela" }

    SenadaTheme {
        SenadaBackground {
            Box(modifier = Modifier.fillMaxSize()) {
                SongsScreen(
                    songs = songs,
                    activeSong = activeSong,
                    isPlaying = true,
                    artworkProvider = { null },
                    onSongClick = {}
                )
                FloatingDock(
                    currentTab = NavTab.LAGU,
                    onTabSelected = {},
                    activeSong = activeSong,
                    isPlaying = true,
                    artworkBitmap = null,
                    onOpenFullPlayer = {},
                    onTogglePlayPause = {},
                    onNext = {},
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview(name = "03 · Folder Screen (412x892)", widthDp = 412, heightDp = 892, showBackground = true)
@Composable
fun Preview03FoldersScreen() {
    val folders = MockMusicData.sampleFolders
    val activeSong = MockMusicData.sampleSongs.first { it.title == "Senja di Jendela" }

    SenadaTheme {
        SenadaBackground {
            Box(modifier = Modifier.fillMaxSize()) {
                FoldersScreen(
                    folders = folders,
                    onFolderClick = {}
                )
                FloatingDock(
                    currentTab = NavTab.FOLDER,
                    onTabSelected = {},
                    activeSong = activeSong,
                    isPlaying = true,
                    artworkBitmap = null,
                    onOpenFullPlayer = {},
                    onTogglePlayPause = {},
                    onNext = {},
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview(name = "04 · Isi Folder (412x892)", widthDp = 412, heightDp = 892, showBackground = true)
@Composable
fun Preview04FolderDetailScreen() {
    val folder = MockMusicData.sampleFolders.first { it.name == "Music" }
    val activeSong = MockMusicData.sampleSongs.first { it.title == "Senja di Jendela" }

    SenadaTheme {
        SenadaBackground {
            Box(modifier = Modifier.fillMaxSize()) {
                FolderDetailScreen(
                    folder = folder,
                    activeSong = activeSong,
                    isPlaying = true,
                    artworkProvider = { null },
                    onBack = {},
                    onSongClick = {}
                )
                FloatingDock(
                    currentTab = NavTab.FOLDER,
                    onTabSelected = {},
                    activeSong = activeSong,
                    isPlaying = true,
                    artworkBitmap = null,
                    onOpenFullPlayer = {},
                    onTogglePlayPause = {},
                    onNext = {},
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview(name = "05 · Cari Screen (412x892)", widthDp = 412, heightDp = 892, showBackground = true)
@Composable
fun Preview05SearchScreen() {
    val songs = MockMusicData.sampleSongs
    val activeSong = songs.first { it.title == "Senja di Jendela" }

    SenadaTheme {
        SenadaBackground {
            Box(modifier = Modifier.fillMaxSize()) {
                SearchScreen(
                    songs = songs,
                    activeSong = activeSong,
                    isPlaying = true,
                    artworkProvider = { null },
                    onSongClick = {}
                )
                FloatingDock(
                    currentTab = NavTab.CARI,
                    onTabSelected = {},
                    activeSong = activeSong,
                    isPlaying = true,
                    artworkBitmap = null,
                    onOpenFullPlayer = {},
                    onTogglePlayPause = {},
                    onNext = {},
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview(name = "08 · Pemutar Penuh Memutar (412x892)", widthDp = 412, heightDp = 892, showBackground = true)
@Composable
fun Preview08FullPlayerPlaying() {
    val song = MockMusicData.sampleSongs.first { it.title == "Senja di Jendela" }
    val playbackState = PlaybackState(
        activeSong = song,
        isPlaying = true,
        currentPositionMs = 98000L, // 1:38
        durationMs = 252000L,       // 4:12
        shuffleMode = true,
        repeatMode = RepeatMode.OFF
    )

    SenadaTheme {
        FullPlayerScreen(
            song = song,
            playbackState = playbackState,
            artworkBitmap = null,
            artworkColors = ArtworkColors.Default,
            onClose = {},
            onTogglePlayPause = {},
            onToggleShuffle = {},
            onCycleRepeat = {},
            onPrevious = {},
            onNext = {},
            onSeek = {}
        )
    }
}

@Preview(name = "18 · Pemutar Penuh Jeda / Gelap (412x892)", widthDp = 412, heightDp = 892, showBackground = true)
@Composable
fun Preview18FullPlayerPausedDark() {
    val song = MockMusicData.sampleSongs.first { it.title == "Ruang Teduh" }
    val playbackState = PlaybackState(
        activeSong = song,
        isPlaying = false,
        currentPositionMs = 0L,     // 0:00
        durationMs = 301000L,       // 5:01
        shuffleMode = false,
        repeatMode = RepeatMode.ONE
    )
    val darkColors = ArtworkColors(
        topColor = androidx.compose.ui.graphics.Color(0xFF38473D),
        middleColor = androidx.compose.ui.graphics.Color(0xFF4C5B50),
        bottomColor = androidx.compose.ui.graphics.Color(0xFF6B7E6F),
        isDarkBackground = true
    )

    SenadaTheme {
        FullPlayerScreen(
            song = song,
            playbackState = playbackState,
            artworkBitmap = null,
            artworkColors = darkColors,
            onClose = {},
            onTogglePlayPause = {},
            onToggleShuffle = {},
            onCycleRepeat = {},
            onPrevious = {},
            onNext = {},
            onSeek = {}
        )
    }
}

@Preview(name = "09 · Izin Akses (412x892)", widthDp = 412, heightDp = 892, showBackground = true)
@Composable
fun Preview09PermissionRequest() {
    SenadaTheme {
        SenadaBackground {
            PermissionRequestScreen(onRequestPermission = {})
        }
    }
}

@Preview(name = "10 · Izin Ditolak (412x892)", widthDp = 412, heightDp = 892, showBackground = true)
@Composable
fun Preview10PermissionDenied() {
    SenadaTheme {
        SenadaBackground {
            PermissionDeniedScreen(onRequestPermission = {}, onOpenSettings = {})
        }
    }
}

@Preview(name = "11 · Pemindaian (412x892)", widthDp = 412, heightDp = 892, showBackground = true)
@Composable
fun Preview11Scanning() {
    SenadaTheme {
        SenadaBackground {
            ScanningScreen()
        }
    }
}

@Preview(name = "12 · Koleksi Kosong (412x892)", widthDp = 412, heightDp = 892, showBackground = true)
@Composable
fun Preview12EmptyCollection() {
    SenadaTheme {
        SenadaBackground {
            Box(modifier = Modifier.fillMaxSize()) {
                EmptyCollectionScreen()
                FloatingDock(
                    currentTab = NavTab.HOME,
                    onTabSelected = {},
                    activeSong = null,
                    isPlaying = false,
                    artworkBitmap = null,
                    onOpenFullPlayer = {},
                    onTogglePlayPause = {},
                    onNext = {},
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview(name = "13 · File Hilang Snackbar (412x892)", widthDp = 412, heightDp = 892, showBackground = true)
@Composable
fun Preview13MissingFileSnackbar() {
    val songs = MockMusicData.sampleSongs
    val activeSong = songs.first { it.title == "Senja di Jendela" }

    SenadaTheme {
        SenadaBackground {
            Box(modifier = Modifier.fillMaxSize()) {
                HomeScreen(
                    songs = songs,
                    activeSong = activeSong,
                    isPlaying = true,
                    artworkProvider = { null },
                    onSongClick = {}
                )
                MissingFileSnackbar(
                    message = "File lagu tidak tersedia.\nMemutar lagu berikutnya.",
                    onDismiss = {},
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 160.dp)
                )
                FloatingDock(
                    currentTab = NavTab.HOME,
                    onTabSelected = {},
                    activeSong = activeSong,
                    isPlaying = true,
                    artworkBitmap = null,
                    onOpenFullPlayer = {},
                    onTogglePlayPause = {},
                    onNext = {},
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
