package com.senada.music.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Konfigurasi tipografi Senada (Sumber kebenaran: SzO06 02 / Tipografi).
 *
 * Catatan Lisensi & Fallback Font:
 * Font produk yang dispesifikasikan adalah SF Pro Display.
 * Konfigurasi font diisolasikan di sini. Jika file font lokal belum dibundel untuk
 * kepatuhan lisensi platform, digunakan FontFamily.SansSerif (Inter / Roboto fallback sistem).
 */
val SenadaFontFamily = FontFamily.SansSerif

val ScreenTitle = TextStyle(
    fontFamily = SenadaFontFamily,
    fontWeight = FontWeight.SemiBold, // 600
    fontSize = 30.sp,
    lineHeight = 36.sp,
    color = Ink
)

val PlayerTitle = TextStyle(
    fontFamily = SenadaFontFamily,
    fontWeight = FontWeight.SemiBold, // 600
    fontSize = 28.sp,
    lineHeight = 34.sp,
    color = Ink
)

val SectionTitle = TextStyle(
    fontFamily = SenadaFontFamily,
    fontWeight = FontWeight.SemiBold, // 600
    fontSize = 20.sp,
    lineHeight = 26.sp,
    color = Ink
)

val SongTitleStyle = TextStyle(
    fontFamily = SenadaFontFamily,
    fontWeight = FontWeight.SemiBold, // 600
    fontSize = 15.sp,
    lineHeight = 20.sp,
    color = Ink
)

val BodyAndButton = TextStyle(
    fontFamily = SenadaFontFamily,
    fontWeight = FontWeight.Medium, // 500
    fontSize = 14.sp,
    lineHeight = 20.sp,
    color = Ink
)

val MetadataStyle = TextStyle(
    fontFamily = SenadaFontFamily,
    fontWeight = FontWeight.Normal, // 400
    fontSize = 12.sp,
    lineHeight = 18.sp,
    color = Muted
)

val NavLabelStyle = TextStyle(
    fontFamily = SenadaFontFamily,
    fontWeight = FontWeight.Medium, // 500
    fontSize = 11.sp,
    lineHeight = 16.sp,
    color = Ink
)

val SenadaTypography = Typography(
    headlineLarge = ScreenTitle,
    headlineMedium = PlayerTitle,
    titleLarge = SectionTitle,
    titleMedium = SongTitleStyle,
    bodyMedium = BodyAndButton,
    bodySmall = MetadataStyle,
    labelSmall = NavLabelStyle
)
