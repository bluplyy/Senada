package com.senada.music.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.senada.music.ui.theme.BgBottom
import com.senada.music.ui.theme.Cream

@Composable
fun SenadaApp(
    viewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionStatus by viewModel.permissionStatus.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val folders by viewModel.folders.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedFolder by viewModel.selectedFolder.collectAsState()
    val isFullPlayerVisible by viewModel.isFullPlayerVisible.collectAsState()
    val activeArtworkBitmap by viewModel.activeArtworkBitmap.collectAsState()
    val activeArtworkColors by viewModel.activeArtworkColors.collectAsState()

    val targetPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onPermissionGranted()
        } else {
            val activity = context as? androidx.activity.ComponentActivity
            val shouldShowRationale = activity?.shouldShowRequestPermissionRationale(targetPermission) ?: false
            viewModel.onPermissionDenied(shouldShowRationale)
        }
    }

    fun checkAndRequestPermission() {
        val isGranted = ContextCompat.checkSelfPermission(context, targetPermission) == PackageManager.PERMISSION_GRANTED
        if (isGranted) {
            viewModel.onPermissionGranted()
        } else {
            permissionLauncher.launch(targetPermission)
        }
    }

    // Cek ulang izin saat pengguna kembali dari halaman Pengaturan
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val isGranted = ContextCompat.checkSelfPermission(context, targetPermission) == PackageManager.PERMISSION_GRANTED
                if (isGranted && permissionStatus != PermissionStatus.GRANTED) {
                    viewModel.onPermissionGranted()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Penanganan Back button hardware / gesture
    BackHandler(enabled = isFullPlayerVisible) {
        viewModel.closeFullPlayer()
    }

    BackHandler(enabled = selectedFolder != null && !isFullPlayerVisible) {
        viewModel.selectFolder(null)
    }

    SenadaBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                // 09 · Izin akses belum diminta
                permissionStatus == PermissionStatus.INITIAL -> {
                    PermissionRequestScreen(
                        onRequestPermission = { checkAndRequestPermission() }
                    )
                }

                // 10 · Izin akses ditolak
                permissionStatus == PermissionStatus.DENIED || permissionStatus == PermissionStatus.NEED_SETTINGS -> {
                    PermissionDeniedScreen(
                        onRequestPermission = { checkAndRequestPermission() },
                        onOpenSettings = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    )
                }

                // 11 · Pemindaian awal
                isScanning -> {
                    ScanningScreen()
                }

                // 12 · Koleksi kosong
                songs.isEmpty() -> {
                    EmptyCollectionScreen()
                    FloatingDock(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.selectTab(it) },
                        activeSong = null,
                        isPlaying = false,
                        artworkBitmap = null,
                        onOpenFullPlayer = {},
                        onTogglePlayPause = {},
                        onNext = {},
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                // Konten utama (Home, Lagu, Folder, Cari)
                else -> {
                    when (currentTab) {
                        NavTab.HOME -> {
                            HomeScreen(
                                songs = songs,
                                activeSong = playbackState.activeSong,
                                isPlaying = playbackState.isPlaying,
                                artworkProvider = { viewModel.getArtworkForSong(it) },
                                onSongClick = { viewModel.playSong(it, songs) }
                            )
                        }

                        NavTab.LAGU -> {
                            SongsScreen(
                                songs = songs,
                                activeSong = playbackState.activeSong,
                                isPlaying = playbackState.isPlaying,
                                artworkProvider = { viewModel.getArtworkForSong(it) },
                                onSongClick = { viewModel.playSong(it, songs) }
                            )
                        }

                        NavTab.FOLDER -> {
                            if (selectedFolder != null) {
                                FolderDetailScreen(
                                    folder = selectedFolder!!,
                                    activeSong = playbackState.activeSong,
                                    isPlaying = playbackState.isPlaying,
                                    artworkProvider = { viewModel.getArtworkForSong(it) },
                                    onBack = { viewModel.selectFolder(null) },
                                    onSongClick = { viewModel.playSong(it, selectedFolder!!.songs) }
                                )
                            } else {
                                FoldersScreen(
                                    folders = folders,
                                    onFolderClick = { viewModel.selectFolder(it) }
                                )
                            }
                        }

                        NavTab.CARI -> {
                            SearchScreen(
                                songs = songs,
                                activeSong = playbackState.activeSong,
                                isPlaying = playbackState.isPlaying,
                                artworkProvider = { viewModel.getArtworkForSong(it) },
                                onSongClick = { viewModel.playSong(it, songs) }
                            )
                        }
                    }

                    // Snackbar jika file hilang (Frame 13) di atas floating dock
                    playbackState.errorMessage?.let { errorMsg ->
                        MissingFileSnackbar(
                            message = errorMsg,
                            onDismiss = { viewModel.clearSnackbar() },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 160.dp)
                        )
                    }

                    // Scrim difusi lembut di belakang floating dock untuk efek frosted glass yang realistis
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    0.0f to Color.Transparent,
                                    0.4f to Cream.copy(alpha = 0.35f),
                                    1.0f to Cream.copy(alpha = 0.85f)
                                )
                            )
                    )

                    // Floating Dock (Mini Player + Navigasi)
                    FloatingDock(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.selectTab(it) },
                        activeSong = playbackState.activeSong,
                        isPlaying = playbackState.isPlaying,
                        artworkBitmap = activeArtworkBitmap,
                        onOpenFullPlayer = { viewModel.openFullPlayer() },
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onNext = { viewModel.skipToNext() },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }

            // Layar Pemutar Penuh (Frames 08 & 18) sebagai overlay transisi vertikal
            AnimatedVisibility(
                visible = isFullPlayerVisible && playbackState.activeSong != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.fillMaxSize()
            ) {
                playbackState.activeSong?.let { song ->
                    FullPlayerScreen(
                        song = song,
                        playbackState = playbackState,
                        artworkBitmap = activeArtworkBitmap,
                        artworkColors = activeArtworkColors,
                        onClose = { viewModel.closeFullPlayer() },
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onToggleShuffle = { viewModel.toggleShuffle() },
                        onCycleRepeat = { viewModel.cycleRepeatMode() },
                        onPrevious = { viewModel.skipToPrevious() },
                        onNext = { viewModel.skipToNext() },
                        onSeek = { viewModel.seekTo(it) }
                    )
                }
            }
        }
    }
}
