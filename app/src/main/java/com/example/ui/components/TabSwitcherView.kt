package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrowserTab
import com.example.ui.theme.UberDarkBorder
import com.example.ui.theme.UberGray400
import com.example.ui.theme.UberGray600
import com.example.ui.theme.UberNearBlack
import com.example.ui.theme.UberPureBlack
import com.example.ui.theme.UberPureWhite
import com.example.ui.theme.UberPurple

@Composable
fun TabSwitcherView(
    tabs: List<BrowserTab>,
    activeTabId: String,
    onTabSelect: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: (isPrivate: Boolean) -> Unit,
    onCloseAll: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {
            // Header in Uber bold typography
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tabs",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "${tabs.size} active • Memory optimized",
                        color = UberGray400,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onCloseAll,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Close All Tabs",
                            tint = UberGray400
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Uber Pill Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onNewTab(false) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("new_tab_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "New Tab",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = { onNewTab(true) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("new_private_tab_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = UberPurple
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, UberPurple.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VisibilityOff,
                        contentDescription = null,
                        tint = UberPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Private Tab",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Grid of Tabs
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(tabs, key = { it.id }) { tab ->
                    TabCard(
                        tab = tab,
                        isActive = tab.id == activeTabId,
                        onSelect = { onTabSelect(tab.id) },
                        onClose = { onCloseTab(tab.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TabCard(
    tab: BrowserTab,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    val borderColor = when {
        isActive && tab.isPrivate -> UberPurple
        isActive -> MaterialTheme.colorScheme.onSurface
        tab.isPrivate -> UberPurple.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .border(if (isActive) 2.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .testTag("tab_item_${tab.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tab.isPrivate) UberNearBlack else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (tab.isPrivate) UberPurple.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (tab.isPrivate) {
                        Icon(
                            imageVector = Icons.Outlined.VisibilityOff,
                            contentDescription = "Private",
                            tint = UberPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = tab.title.ifEmpty { "New Tab" },
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.5.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Tab",
                        tint = UberGray400,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // Tab Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val initial = tab.title.firstOrNull()?.uppercase() ?: "A"
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (tab.isPrivate) UberPurple.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            color = if (tab.isPrivate) UberPurple else MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (tab.url == "about:home") "Dashboard" else tab.url.removePrefix("https://").removePrefix("http://"),
                        color = UberGray400,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (isActive) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (tab.isPrivate) UberPurple else MaterialTheme.colorScheme.onSurface)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = if (tab.isPrivate) UberPureWhite else MaterialTheme.colorScheme.surface,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
