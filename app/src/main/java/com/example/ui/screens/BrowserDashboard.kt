package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ShortcutItem
import com.example.data.model.WallpaperType
import com.example.data.model.WidgetPersonalization
import com.example.ui.components.DashboardWallpaper
import com.example.ui.components.DashboardWidgetDeck
import com.example.ui.theme.UberBlue
import com.example.ui.theme.UberDarkBorder
import com.example.ui.theme.UberDarkSurfaceVariant
import com.example.ui.theme.UberGray400
import com.example.ui.theme.UberGreen
import com.example.ui.theme.UberNearBlack
import com.example.ui.theme.UberPureBlack
import com.example.ui.theme.UberPureWhite
import com.example.ui.theme.UberPurple

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrowserDashboard(
    shortcuts: List<ShortcutItem>,
    isPrivate: Boolean,
    searchEngine: String,
    adsBlockedCount: Int,
    trackersBlockedCount: Int,
    dataSavedKb: Long,
    isPowerSaverActive: Boolean,
    currentWallpaper: WallpaperType,
    widgetConfig: WidgetPersonalization,
    onSearchClick: () -> Unit,
    onShortcutClick: (ShortcutItem) -> Unit,
    onDeleteShortcut: (ShortcutItem) -> Unit,
    onAddShortcutClick: () -> Unit,
    onTogglePrivateMode: () -> Unit,
    onOpenDevTools: () -> Unit,
    onOpenVault: () -> Unit,
    onOpenRecorder: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenAccount: () -> Unit,
    onSetWallpaper: (WallpaperType) -> Unit,
    onUpdateWidgetConfig: (WidgetPersonalization) -> Unit,
    modifier: Modifier = Modifier
) {
    var showWallpaperDialog by remember { mutableStateOf(false) }
    var showPersonalizeDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        // Animated Random / Selected Wallpaper in background
        DashboardWallpaper(
            wallpaperType = if (isPrivate) WallpaperType.MINIMAL_SLATE else currentWallpaper
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Arton Wordmark Header with Wallpaper & Account Action Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Arton",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = if (isPrivate) "Private Mode Active" else "Fast. Private. Shielded.",
                        color = if (isPrivate) UberPurple else UberGray400,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Wallpaper quick button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), CircleShape)
                            .clickable { showWallpaperDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Change Wallpaper",
                            tint = UberBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Scynr Account Avatar Badge
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(UberDarkSurfaceVariant)
                            .border(1.dp, UberDarkBorder, CircleShape)
                            .clickable { onOpenAccount() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_arton_logo),
                            contentDescription = "Arton Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Signature "Where to online?" Search Capsule
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .clickable { onSearchClick() }
                    .border(
                        width = 1.dp,
                        color = if (isPrivate) UberPurple.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(32.dp)
                    ),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isPrivate) "Search anonymously..." else "Where to online?",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Search Engine pill badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = searchEngine,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Animated Modern Widgets: Weather, Recent News & Game Matches
            DashboardWidgetDeck(
                config = widgetConfig,
                onOpenArticle = { url -> onShortcutClick(ShortcutItem(title = "News", url = url)) },
                onOpenMatch = { url -> onShortcutClick(ShortcutItem(title = "Matches", url = url)) },
                onPersonalizeClick = { showPersonalizeDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Arton Shield Protection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(UberPureBlack),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = UberGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Arton Shield Protection",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (isPowerSaverActive) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(UberGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.BatterySaver,
                                        contentDescription = null,
                                        tint = UberGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Eco Active",
                                        color = UberGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        UberMetricItem(value = "$adsBlockedCount", label = "Ads blocked", highlightColor = UberGreen)
                        UberMetricItem(value = "$trackersBlockedCount", label = "Trackers stopped", highlightColor = UberBlue)
                        UberMetricItem(value = "${dataSavedKb / 1024} MB", label = "Data saved", highlightColor = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Quick Tools: Downloads, Vault, DevTools, Recorder
            Text(
                text = "Quick Tools",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UberActionCard(
                    title = "Downloads",
                    subtitle = "Fast & Torrents",
                    icon = Icons.Default.Download,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenDownloads
                )
                UberActionCard(
                    title = "URL Lock Vault",
                    subtitle = "Biometric protected",
                    icon = Icons.Default.Lock,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenVault
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UberActionCard(
                    title = "Inspect Element",
                    subtitle = "Developer tools",
                    icon = Icons.Default.Code,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenDevTools
                )
                UberActionCard(
                    title = "Scynr Cloud",
                    subtitle = "Cloud sync & backup",
                    icon = Icons.Default.Sync,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAccount
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Suggestions (Placed Down & Smaller as requested)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Suggestions",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "+ Add",
                    color = if (isPrivate) UberPurple else UberBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onAddShortcutClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("add_shortcut_button")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Compact / Smaller Suggestions Grid
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 4
            ) {
                shortcuts.forEach { shortcut ->
                    CompactTileCard(
                        shortcut = shortcut,
                        onClick = { onShortcutClick(shortcut) }
                    )
                }

                // Add tile in matching compact format
                Box(
                    modifier = Modifier
                        .size(width = 72.dp, height = 66.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { onAddShortcutClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 7. Private Browsing Switch Banner at bottom
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onTogglePrivateMode() }
                    .border(
                        width = 1.dp,
                        color = if (isPrivate) UberPurple.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isPrivate) UberPurple.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.VisibilityOff,
                            contentDescription = null,
                            tint = if (isPrivate) UberPurple else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isPrivate) "Exit Private Browsing" else "Go Private & Anonymous",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isPrivate) "Switch to standard session" else "No history, no cookies, isolated tabs",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Wallpaper Selection Dialog
    if (showWallpaperDialog) {
        AlertDialog(
            onDismissRequest = { showWallpaperDialog = false },
            title = { Text("Choose Wallpaper", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        WallpaperType.AURORA to "Aurora Waves (Animated)",
                        WallpaperType.NEON_MESH to "Neon Cyber Mesh (Animated)",
                        WallpaperType.SUNSET to "Sunset Horizon (Animated)",
                        WallpaperType.COSMIC to "Cosmic Starlight (Twinkling)",
                        WallpaperType.MINIMAL_SLATE to "Minimalist Obsidian"
                    ).forEach { (type, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (currentWallpaper == type) UberBlue.copy(alpha = 0.15f)
                                    else Color.Transparent
                                )
                                .clickable {
                                    onSetWallpaper(type)
                                    showWallpaperDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                color = if (currentWallpaper == type) UberBlue else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (currentWallpaper == type) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWallpaperDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Widgets Personalization Dialog
    if (showPersonalizeDialog) {
        AlertDialog(
            onDismissRequest = { showPersonalizeDialog = false },
            title = { Text("Personalize Live Deck", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Weather Widget", fontSize = 14.sp)
                        Switch(
                            checked = widgetConfig.showWeather,
                            onCheckedChange = { onUpdateWidgetConfig(widgetConfig.copy(showWeather = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = UberPureWhite, checkedTrackColor = UberBlue)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Trending News", fontSize = 14.sp)
                        Switch(
                            checked = widgetConfig.showNews,
                            onCheckedChange = { onUpdateWidgetConfig(widgetConfig.copy(showNews = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = UberPureWhite, checkedTrackColor = UberBlue)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Game Matches Tracker", fontSize = 14.sp)
                        Switch(
                            checked = widgetConfig.showMatches,
                            onCheckedChange = { onUpdateWidgetConfig(widgetConfig.copy(showMatches = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = UberPureWhite, checkedTrackColor = UberBlue)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPersonalizeDialog = false }) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun CompactTileCard(
    shortcut: ShortcutItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 72.dp, height = 66.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        try {
                            Color(android.graphics.Color.parseColor(shortcut.colorHex))
                        } catch (e: Exception) {
                            UberBlue
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = shortcut.title.take(1).uppercase(),
                    color = UberPureWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = shortcut.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun UberMetricItem(value: String, label: String, highlightColor: Color) {
    Column {
        Text(text = value, color = highlightColor, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun UberActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = UberBlue,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
