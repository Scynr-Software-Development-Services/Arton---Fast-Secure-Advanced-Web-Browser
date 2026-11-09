package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OrbCyan
import com.example.ui.theme.OrbPink
import com.example.ui.theme.OrbPurple
import com.example.ui.theme.UberGray400
import com.example.ui.theme.UberPureBlack
import com.example.ui.theme.UberPureWhite

@Composable
fun ArtonBottomBar(
    tabsCount: Int,
    isTabSheetOpen: Boolean,
    isPrivate: Boolean,
    onHistoryClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onCenterOrbClick: () -> Unit,
    onTabCounterClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. History / Clock button `🕒` (or `🕶` incognito)
            IconButton(
                onClick = onHistoryClick,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("bottom_history_btn")
            ) {
                Icon(
                    imageVector = if (isPrivate) Icons.Outlined.VisibilityOff else Icons.Outlined.History,
                    contentDescription = "History",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(23.dp)
                )
            }

            // 2. Pin / Bookmark `📌` / `⭐`
            IconButton(
                onClick = onBookmarksClick,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("bottom_bookmarks_btn")
            ) {
                Icon(
                    imageVector = Icons.Outlined.PushPin,
                    contentDescription = "Bookmarks",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(23.dp)
                )
            }

            // 3. Center Glowing Iridescent Pearl Orb (Image 2)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onCenterOrbClick() }
                    .testTag("center_orb_action"),
                contentAlignment = Alignment.Center
            ) {
                // Soft glow halo
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    OrbCyan.copy(alpha = 0.5f),
                                    OrbPurple.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // The Pearl Orb
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(8.dp, CircleShape, spotColor = OrbPurple)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    OrbCyan,
                                    OrbPurple,
                                    OrbPink
                                )
                            )
                        )
                ) {
                    // Specular inner gleam
                    Canvas(modifier = Modifier.size(36.dp)) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.55f),
                            radius = size.minDimension * 0.28f,
                            center = androidx.compose.ui.geometry.Offset(
                                size.width * 0.35f,
                                size.height * 0.32f
                            )
                        )
                    }
                }
            }

            // 4. Tab Count Squircle `[ 20 ]` (turns solid black when tab sheet is open!)
            val tabBg by animateColorAsState(
                targetValue = if (isTabSheetOpen) UberPureBlack else Color.Transparent,
                label = "tabBg"
            )
            val tabTextColor by animateColorAsState(
                targetValue = if (isTabSheetOpen) UberPureWhite else MaterialTheme.colorScheme.onSurface,
                label = "tabText"
            )

            Box(
                modifier = Modifier
                    .size(width = 34.dp, height = 34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tabBg)
                    .border(
                        width = 1.5.dp,
                        color = if (isTabSheetOpen) UberPureBlack else MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onTabCounterClick() }
                    .testTag("bottom_tabs_btn"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$tabsCount",
                    color = tabTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 5. More Options `···` / `☰`
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("bottom_menu_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "Menu Options",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
