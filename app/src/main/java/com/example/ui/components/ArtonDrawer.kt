package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.WhmcsUser
import com.example.ui.theme.UberBlue
import com.example.ui.theme.UberGray400
import com.example.ui.theme.UberGray600
import com.example.ui.theme.UberGreen
import com.example.ui.theme.UberPureBlack
import com.example.ui.theme.UberPureWhite
import com.example.ui.theme.UberPurple
import com.example.ui.theme.UberYellow

@Composable
fun ArtonDrawer(
    whmcsUser: WhmcsUser,
    tabsCount: Int,
    isDarkTheme: Boolean,
    isAdBlockEnabled: Boolean,
    isPowerSaverEnabled: Boolean,
    isForceDarkWebEnabled: Boolean,
    onNavigateHome: () -> Unit,
    onNewTab: (Boolean) -> Unit,
    onShowTabs: () -> Unit,
    onShowBookmarks: () -> Unit,
    onShowHistory: () -> Unit,
    onShowVault: () -> Unit,
    onShowDevTools: () -> Unit,
    onShowRecorder: () -> Unit,
    onShowDownloads: () -> Unit,
    onShowWhmcsProfile: () -> Unit,
    onShowSettings: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleAdBlock: () -> Unit,
    onTogglePowerSaver: () -> Unit,
    onToggleForceDarkWeb: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier
            .width(320.dp)
            .fillMaxHeight(),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 20.dp)
        ) {
            // Uber Profile Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowWhmcsProfile() }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("drawer_profile_header")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (whmcsUser.isLoggedIn) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(36.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_arton_logo),
                                contentDescription = "Arton",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (whmcsUser.isLoggedIn) {
                                "${whmcsUser.firstName} ${whmcsUser.lastName}".trim()
                            } else "SCYNR Account",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Uber-style Rating Pill
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = UberYellow,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (whmcsUser.isLoggedIn) "5.0 VIP" else "Sign In",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (whmcsUser.isLoggedIn) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Synced",
                                    color = UberGreen,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = UberGray400,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Navigation Items in Uber clean list style
            UberDrawerItem(
                icon = Icons.Default.Home,
                title = "Home Dashboard",
                onClick = onNavigateHome
            )

            UberDrawerItem(
                icon = Icons.Outlined.Add,
                title = "New Tab",
                onClick = { onNewTab(false) }
            )

            UberDrawerItem(
                icon = Icons.Outlined.VisibilityOff,
                title = "Private Browsing Tab",
                iconTint = UberPurple,
                onClick = { onNewTab(true) }
            )

            UberDrawerItem(
                icon = Icons.Default.Tab,
                title = "All Open Tabs",
                badgeText = "$tabsCount",
                onClick = onShowTabs
            )

            UberDrawerItem(
                icon = Icons.Default.Bookmark,
                title = "Saved Bookmarks",
                onClick = onShowBookmarks
            )

            UberDrawerItem(
                icon = Icons.Default.History,
                title = "History",
                secondaryNote = "Protected",
                onClick = onShowHistory
            )

            UberDrawerItem(
                icon = Icons.Default.Download,
                title = "Downloads",
                secondaryNote = "Fast & Torrents",
                onClick = onShowDownloads
            )

            UberDrawerItem(
                icon = Icons.Default.Lock,
                title = "URL Lock Vault",
                iconTint = UberGreen,
                secondaryNote = "Biometric",
                onClick = onShowVault
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Developer & Safety",
                color = UberGray400,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            UberDrawerItem(
                icon = Icons.Default.Code,
                title = "Inspect Element",
                iconTint = MaterialTheme.colorScheme.onSurface,
                onClick = onShowDevTools
            )

            UberDrawerItem(
                icon = Icons.Default.Videocam,
                title = "Screen Recorder",
                iconTint = UberYellow,
                onClick = onShowRecorder
            )

            UberDrawerItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                onClick = onShowSettings
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Preferences",
                color = UberGray400,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            UberDrawerToggleRow(
                icon = Icons.Default.Shield,
                title = "Ad & Tracker Blocker",
                isChecked = isAdBlockEnabled,
                tint = UberGreen,
                onCheckedChange = { onToggleAdBlock() }
            )

            UberDrawerToggleRow(
                icon = Icons.Default.BatterySaver,
                title = "Low Battery Mode",
                isChecked = isPowerSaverEnabled,
                tint = UberGreen,
                onCheckedChange = { onTogglePowerSaver() }
            )

            UberDrawerToggleRow(
                icon = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                title = if (isDarkTheme) "Dark Mode" else "Light Mode",
                isChecked = isDarkTheme,
                tint = MaterialTheme.colorScheme.onSurface,
                onCheckedChange = { onToggleTheme() }
            )

            UberDrawerToggleRow(
                icon = Icons.Default.Security,
                title = "Force Dark Webpages",
                isChecked = isForceDarkWebEnabled,
                tint = MaterialTheme.colorScheme.onSurface,
                onCheckedChange = { onToggleForceDarkWeb() }
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Arton Browser • Powered by SCYNR",
                color = UberGray600,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun UberDrawerItem(
    icon: ImageVector,
    title: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    badgeText: String? = null,
    secondaryNote: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        if (secondaryNote != null) {
            Text(
                text = secondaryNote,
                color = UberGray400,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 6.dp)
            )
        }
        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = UberGray400,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun UberDrawerToggleRow(
    icon: ImageVector,
    title: String,
    isChecked: Boolean,
    tint: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = tint,
                checkedTrackColor = tint.copy(alpha = 0.35f),
                uncheckedThumbColor = UberGray400,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}
