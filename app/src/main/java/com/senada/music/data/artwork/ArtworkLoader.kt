package com.senada.music.data.artwork

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.LruCache
import android.util.Size
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Palet warna gradasi yang diekstrak dari artwork lokal untuk latar pemutar penuh.
 */
data class ArtworkColors(
    val topColor: Color,
    val middleColor: Color,
    val bottomColor: Color,
    val isDarkBackground: Boolean
) {
    companion object {
        // Fallback default gradasi pemutar (Senja di Jendela / light style)
        val Default = ArtworkColors(
            topColor = Color(0xFFB9BFB8),
            middleColor = Color(0xFFDEB38A),
            bottomColor = Color(0xFF9CADA6),
            isDarkBackground = false
        )

        // Dark style fallback (Ruang Teduh / dark style)
        val DarkSample = ArtworkColors(
            topColor = Color(0xFF283A37),
            middleColor = Color(0xFF627866),
            bottomColor = Color(0xFFB6C4AE),
            isDarkBackground = true
        )
    }
}

/**
 * Pemuat artwork lokal offline dengan caching memori dan ekstraksi warna Palette.
 */
class ArtworkLoader(private val context: Context) {

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8

    private val bitmapCache = object : LruCache<Long, Bitmap>(cacheSize) {
        override fun sizeOf(key: Long, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    private val colorCache = LruCache<Long, ArtworkColors>(64)

    suspend fun loadArtwork(songId: Long, uri: Uri, targetSize: Int = 512): Bitmap? = withContext(Dispatchers.IO) {
        bitmapCache.get(songId)?.let { return@withContext it }

        val bitmap = loadBitmapFromUri(uri, targetSize)
        if (bitmap != null) {
            bitmapCache.put(songId, bitmap)
        }
        bitmap
    }

    suspend fun extractColors(songId: Long, uri: Uri): ArtworkColors = withContext(Dispatchers.IO) {
        colorCache.get(songId)?.let { return@withContext it }

        val bitmap = loadArtwork(songId, uri, targetSize = 128)
        if (bitmap == null) {
            return@withContext ArtworkColors.Default
        }

        val palette = Palette.from(bitmap).generate()

        val dominant = palette.getDominantColor(0xFFECE0DA.toInt())
        val vibrant = palette.getVibrantColor(dominant)
        val darkMuted = palette.getDarkMutedColor(dominant)
        val lightMuted = palette.getLightMutedColor(dominant)
        val muted = palette.getMutedColor(dominant)

        val top = Color(darkMuted)
        val mid = Color(if (vibrant != dominant) vibrant else muted)
        val bottom = Color(lightMuted)

        // Hitung luminance top area untuk menentukan teks putih atau gelap
        val topLuminance = calculateLuminance(darkMuted)
        val isDark = topLuminance < 0.45f

        val colors = ArtworkColors(
            topColor = top,
            middleColor = mid,
            bottomColor = bottom,
            isDarkBackground = isDark
        )

        colorCache.put(songId, colors)
        colors
    }

    private fun loadBitmapFromUri(uri: Uri, targetSize: Int): Bitmap? {
        // Coba ContentResolver.loadThumbnail pada API 29+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                return context.contentResolver.loadThumbnail(uri, Size(targetSize, targetSize), null)
            } catch (ignored: Exception) {}
        }

        // Fallback menggunakan MediaMetadataRetriever
        var retriever: MediaMetadataRetriever? = null
        try {
            retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val pictureBytes = retriever.embeddedPicture
            if (pictureBytes != null) {
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.size, options)
                options.inSampleSize = calculateInSampleSize(options, targetSize, targetSize)
                options.inJustDecodeBounds = false
                return BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.size, options)
            }
        } catch (ignored: Exception) {
        } finally {
            try {
                retriever?.release()
            } catch (ignored: Exception) {}
        }

        return null
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun calculateLuminance(colorInt: Int): Float {
        val r = (colorInt shr 16 and 0xFF) / 255.0f
        val g = (colorInt shr 8 and 0xFF) / 255.0f
        val b = (colorInt and 0xFF) / 255.0f
        return 0.299f * r + 0.587f * g + 0.114f * b
    }
}
