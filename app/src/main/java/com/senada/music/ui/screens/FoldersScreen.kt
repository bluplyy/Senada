package com.senada.music.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.senada.music.data.model.Folder
import com.senada.music.ui.components.FolderRow
import com.senada.music.ui.theme.MetadataStyle
import com.senada.music.ui.theme.ScreenTitle

/**
 * Layar 03 · Folder perangkat:
 * Menampilkan daftar folder asli di penyimpanan perangkat yang memiliki file audio.
 */
@Composable
fun FoldersScreen(
    folders: List<Folder>,
    onFolderClick: (Folder) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Folder",
            style = ScreenTitle
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Folder asli yang berisi musik",
            style = MetadataStyle
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = folders,
                key = { it.bucketId }
            ) { folder ->
                FolderRow(
                    folder = folder,
                    onClick = { onFolderClick(folder) }
                )
            }
        }

        // Catatan footer sesuai frame 03
        Text(
            text = "Mengikuti penyimpanan perangkat.\nIsi diperbarui otomatis saat file berubah.",
            style = MetadataStyle,
            modifier = Modifier.padding(bottom = 170.dp, start = 4.dp)
        )
    }
}
