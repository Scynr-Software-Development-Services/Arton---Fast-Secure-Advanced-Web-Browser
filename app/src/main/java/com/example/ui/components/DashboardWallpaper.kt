package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.WallpaperType

@Composable
fun DashboardWallpaper(
    wallpaperType: WallpaperType,
    customPhotoUri: String? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wallpaper_anim")

    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wallpaper_shift"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wallpaper_pulse"
    )

    Box(modifier = modifier.fillMaxSize()) {
        when {
            wallpaperType == WallpaperType.CUSTOM_PHOTO && !customPhotoUri.isNullOrBlank() -> {
                Image(
                    painter = rememberAsyncImagePainter(model = customPhotoUri),
                    contentDescription = "Custom Wallpaper",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Dark subtle scrim so UI text remains crystal clear
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f))
                )
            }
            wallpaperType == WallpaperType.AURORA -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val center1 = Offset(w * (0.2f + 0.6f * animProgress), h * (0.15f + 0.3f * animProgress))
                    val center2 = Offset(w * (0.8f - 0.5f * animProgress), h * (0.7f - 0.25f * animProgress))

                    drawRect(color = Color(0xFF090D16))

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00E5FF).copy(alpha = 0.35f),
                                Color(0xFF1B2DE8).copy(alpha = 0.20f),
                                Color.Transparent
                            ),
                            center = center1,
                            radius = w * 0.9f * pulseScale
                        )
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF8A2BE2).copy(alpha = 0.30f),
                                Color(0xFF00FFA3).copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = center2,
                            radius = w * 0.8f
                        )
                    )
                }
            }
            wallpaperType == WallpaperType.NEON_MESH -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    drawRect(color = Color(0xFF0B0B10))

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF2979FF).copy(alpha = 0.32f), Color.Transparent),
                            center = Offset(w * 0.85f * animProgress, h * 0.25f),
                            radius = w * 0.75f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFF007F).copy(alpha = 0.26f), Color.Transparent),
                            center = Offset(w * (1f - animProgress), h * 0.65f),
                            radius = w * 0.85f
                        )
                    )
                }
            }
            wallpaperType == WallpaperType.SUNSET -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF140C1A),
                                Color(0xFF241026),
                                Color(0xFF100B14)
                            )
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF5E3A).copy(alpha = 0.38f),
                                Color(0xFFFF2A6D).copy(alpha = 0.22f),
                                Color.Transparent
                            ),
                            center = Offset(w * 0.5f, h * (0.3f + 0.15f * animProgress)),
                            radius = w * 0.85f * pulseScale
                        )
                    )
                }
            }
            wallpaperType == WallpaperType.COSMIC -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    drawRect(color = Color(0xFF05060A))

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF6200EA).copy(alpha = 0.28f), Color.Transparent),
                            center = Offset(w * 0.3f, h * 0.4f),
                            radius = w * 0.7f
                        )
                    )

                    // Draw sparkling star particles
                    val starOffsets = listOf(
                        Offset(w * 0.15f, h * 0.12f),
                        Offset(w * 0.75f, h * 0.18f),
                        Offset(w * 0.45f, h * 0.28f),
                        Offset(w * 0.88f, h * 0.42f),
                        Offset(w * 0.22f, h * 0.65f),
                        Offset(w * 0.62f, h * 0.72f),
                        Offset(w * 0.35f, h * 0.85f),
                        Offset(w * 0.82f, h * 0.90f)
                    )

                    starOffsets.forEachIndexed { idx, pt ->
                        val twinkleAlpha = ((animProgress + idx * 0.15f) % 1f)
                        drawCircle(
                            color = Color.White.copy(alpha = 0.3f + 0.7f * twinkleAlpha),
                            radius = if (idx % 2 == 0) 2.5f else 1.8f,
                            center = pt
                        )
                    }
                }
            }
            else -> { // MINIMAL_SLATE default
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0xFF0D0F14))
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF276EF1).copy(alpha = 0.12f), Color.Transparent),
                            center = Offset(size.width * 0.5f, size.height * (0.2f + 0.1f * animProgress)),
                            radius = size.width * 0.8f
                        )
                    )
                }
            }
        }
    }
}
