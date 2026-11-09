package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WallpaperType
import com.example.data.model.WidgetPersonalization
import com.example.ui.theme.UberBlue
import com.example.ui.theme.UberGreen
import com.example.ui.theme.UberPureBlack
import com.example.ui.theme.UberPureWhite

@Composable
fun SettingsScreen(
    searchEngine: String,
    isDarkTheme: Boolean,
    isAdBlockEnabled: Boolean,
    isPowerSaverEnabled: Boolean,
    isForceDarkWebEnabled: Boolean,
    currentWallpaper: WallpaperType,
    widgetConfig: WidgetPersonalization,
    onSetSearchEngine: (String) -> Unit,
    onToggleDarkTheme: () -> Unit,
    onToggleAdBlock: () -> Unit,
    onTogglePowerSaver: () -> Unit,
    onToggleForceDarkWeb: () -> Unit,
    onSetWallpaper: (WallpaperType) -> Unit,
    onUpdateWidgetConfig: (WidgetPersonalization) -> Unit,
    onClearAllData: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Settings",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Preferences, privacy & personalization",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Appearance & Animated Wallpapers
            SettingsSectionHeader(title = "Appearance & Wallpapers", icon = Icons.Default.Palette)
            SettingsCard {
                SettingsSwitchRow(
                    title = "Dark Theme",
                    subtitle = "High contrast modern dark UI",
                    checked = isDarkTheme,
                    onCheckedChange = { onToggleDarkTheme() }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                SettingsSwitchRow(
                    title = "Force Dark Web Content",
                    subtitle = "Applies dark shaders to light websites",
                    checked = isForceDarkWebEnabled,
                    onCheckedChange = { onToggleForceDarkWeb() }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                // Wallpaper selector
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Dashboard Animated Wallpaper",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Smooth ambient background motion",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            WallpaperType.AURORA to "Aurora",
                            WallpaperType.NEON_MESH to "Neon",
                            WallpaperType.SUNSET to "Sunset",
                            WallpaperType.COSMIC to "Cosmic",
                            WallpaperType.MINIMAL_SLATE to "Slate"
                        ).forEach { (type, label) ->
                            val isSelected = currentWallpaper == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) UberBlue else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { onSetWallpaper(type) }
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) UberPureWhite else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 2. Dashboard Widgets Personalization
            SettingsSectionHeader(title = "Dashboard Widgets", icon = Icons.Default.Widgets)
            SettingsCard {
                SettingsSwitchRow(
                    title = "Weather Widget",
                    subtitle = "Animated temperature and 4-hour forecast",
                    checked = widgetConfig.showWeather,
                    onCheckedChange = { onUpdateWidgetConfig(widgetConfig.copy(showWeather = it)) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                SettingsSwitchRow(
                    title = "Trending News Widget",
                    subtitle = "Real-time tech, AI & cyber headlines",
                    checked = widgetConfig.showNews,
                    onCheckedChange = { onUpdateWidgetConfig(widgetConfig.copy(showNews = it)) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                SettingsSwitchRow(
                    title = "Game Matches Tracker",
                    subtitle = "Live sports & esports score updates",
                    checked = widgetConfig.showMatches,
                    onCheckedChange = { onUpdateWidgetConfig(widgetConfig.copy(showMatches = it)) }
                )
            }

            // 3. Search Engine
            SettingsSectionHeader(title = "Search & Navigation", icon = Icons.Default.Search)
            SettingsCard {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Default Search Engine",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Google", "DuckDuckGo", "Bing", "Brave").forEach { eng ->
                            val isSelected = searchEngine == eng
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) UberBlue else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { onSetSearchEngine(eng) }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = eng,
                                    color = if (isSelected) UberPureWhite else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 4. Shield & Battery
            SettingsSectionHeader(title = "Shield & Eco Performance", icon = Icons.Default.Shield)
            SettingsCard {
                SettingsSwitchRow(
                    title = "Arton Ad-Block Filter",
                    subtitle = "Blocks third-party banners, popups & video ads",
                    checked = isAdBlockEnabled,
                    onCheckedChange = { onToggleAdBlock() }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                SettingsSwitchRow(
                    title = "Eco Low Battery Mode",
                    subtitle = "Pauses background web timers to save power",
                    checked = isPowerSaverEnabled,
                    onCheckedChange = { onTogglePowerSaver() }
                )
            }

            // 5. Downloads & Security
            SettingsSectionHeader(title = "Downloads & Security", icon = Icons.Default.Download)
            SettingsCard {
                SettingsSwitchRow(
                    title = "Turbo Multi-Thread Download",
                    subtitle = "Splits downloads into 16 parallel chunks",
                    checked = true,
                    onCheckedChange = {}
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                SettingsSwitchRow(
                    title = "Harmful File Warning Shield",
                    subtitle = "Inspects signatures of .exe, .apk & scripts",
                    checked = true,
                    onCheckedChange = {}
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                SettingsSwitchRow(
                    title = "BitTorrent P2P Support",
                    subtitle = "Enables .torrent files and magnet links",
                    checked = true,
                    onCheckedChange = {}
                )
            }

            // 6. Data & Cache
            SettingsSectionHeader(title = "Data & Storage", icon = Icons.Default.Delete)
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClearAllData() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Clear Browsing Data",
                            color = Color(0xFFFF5252),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Clears history, cache, and site cookies",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // About Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Arton Browser v2.4.0 (Build 36)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = "Powered by Scynr Cloud Engine", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = UberBlue, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = UberPureWhite, checkedTrackColor = UberBlue)
        )
    }
}
