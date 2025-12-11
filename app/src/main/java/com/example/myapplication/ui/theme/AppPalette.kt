package com.example.myapplication.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppPalette {
    val BackgroundGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF8FBFF),
            Color(0xFFFFF4FB),
            Color(0xFFF2FFFB)
        ),
        start = Offset.Zero,
        end = Offset(1200f, 2400f)
    )

    val CardSurface = Color(0xFFFFFFFF)
    val CardAltSurface = Color(0xFFF9F5FF)
    val CardOutline = Color(0xFFE0E7FF)

    val TextPrimary = Color(0xFF1A1C3A)
    val TextSecondary = Color(0xFF5B6072)
    val TextMuted = Color(0xFF94A3B8)

    val AccentMint = MintSpark
    val AccentPeach = PeachGlow
    val AccentLilac = LilacPulse
    val AccentRose = BlushRose

    val ChipDefault = Color(0xFFE8EEFF)
    val ChipSelected = Color(0xFFEDFDF8)
}




