package com.senada.music.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senada.music.ui.theme.Action
import com.senada.music.ui.theme.BodyAndButton
import com.senada.music.ui.theme.Ink
import com.senada.music.ui.theme.MetadataStyle
import com.senada.music.ui.theme.Muted
import com.senada.music.ui.theme.Olive
import com.senada.music.ui.theme.PlayerTitle

/**
 * Layar 09 · Izin akses musik:
 * Meminta izin pembacaan media audio lokal perangkat.
 */
@Composable
fun PermissionRequestScreen(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(com.senada.music.R.drawable.ic_logo_senada),
            contentDescription = "senada",
            modifier = Modifier.height(28.dp),
            contentScale = ContentScale.Fit,
            alignment = Alignment.CenterStart
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE2DDD5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Action,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Musikmu,\ntanpa internet.",
                style = PlayerTitle,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Izinkan Senada membaca file audio agar musik di perangkat muncul otomatis.",
                style = BodyAndButton.copy(color = Muted, fontSize = 15.sp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onRequestPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .semantics {
                        contentDescription = "Izinkan akses musik"
                        role = Role.Button
                    },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Action)
            ) {
                Text(
                    text = "Izinkan akses",
                    style = BodyAndButton.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

/**
 * Layar 10 · Izin akses ditolak:
 * Menyediakan tombol coba lagi ("Izinkan akses") dan tautan ke pengaturan ("Buka pengaturan").
 */
@Composable
fun PermissionDeniedScreen(
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(com.senada.music.R.drawable.ic_logo_senada),
            contentDescription = "senada",
            modifier = Modifier.height(28.dp),
            contentScale = ContentScale.Fit,
            alignment = Alignment.CenterStart
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE2DDD5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Action,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Akses musik\nbelum diizinkan",
                style = PlayerTitle,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Senada perlu izin file audio untuk membaca koleksimu. Coba izinkan kembali.",
                style = BodyAndButton.copy(color = Muted, fontSize = 15.sp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRequestPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Action)
            ) {
                Text(
                    text = "Izinkan akses",
                    style = BodyAndButton.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE8E2D9))
                    .clickable(onClick = onOpenSettings)
                    .semantics {
                        contentDescription = "Buka pengaturan aplikasi"
                        role = Role.Button
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Buka pengaturan",
                    style = BodyAndButton.copy(color = Ink, fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Jika dialog izin tidak muncul, buka Pengaturan → Izin → Musik dan audio.",
                style = MetadataStyle
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

/**
 * Layar 11 · Pemindaian awal:
 * Ditampilkan saat memindai MediaStore setelah izin diberikan.
 */
@Composable
fun ScanningScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(com.senada.music.R.drawable.ic_logo_senada),
            contentDescription = "senada",
            modifier = Modifier.height(28.dp),
            contentScale = ContentScale.Fit,
            alignment = Alignment.CenterStart
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE2DDD5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CropFree,
                    contentDescription = null,
                    tint = Action,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Membaca musik\ndi perangkat",
                style = PlayerTitle,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Sedang mencari file audio lokal. Koleksimu akan muncul setelah selesai.",
                style = BodyAndButton.copy(color = Muted, fontSize = 15.sp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Olive,
                trackColor = Color(0x33292E28)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

/**
 * Layar 12 · Koleksi kosong:
 * Ditampilkan jika hasil query tidak menemukan file musik valid.
 * Navigasi tetap aktif di bawah tanpa mini player.
 */
@Composable
fun EmptyCollectionScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(com.senada.music.R.drawable.ic_logo_senada),
            contentDescription = "senada",
            modifier = Modifier.height(28.dp),
            contentScale = ContentScale.Fit,
            alignment = Alignment.CenterStart
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE2DDD5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Action,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Belum ada lagu\ndi perangkat",
                style = PlayerTitle,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tambahkan file musik ke penyimpanan perangkat, misalnya folder Music. Lagu akan terbaca otomatis.",
                style = BodyAndButton.copy(color = Muted, fontSize = 15.sp)
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}
