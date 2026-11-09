package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrowserTab
import com.example.ui.theme.MockupAutumnLime
import com.example.ui.theme.UberDarkBorder
import com.example.ui.theme.UberGray400
import com.example.ui.theme.UberNearBlack
import com.example.ui.theme.UberPureBlack
import com.example.ui.theme.UberPureWhite

@Composable
fun TabSheetView(
    tabs: List<BrowserTab>,
    activeTabId: String,
    onTabSelect: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredTabs = remember(tabs, searchQuery) {
        if (searchQuery.isBlank()) tabs
        else tabs.filter { it.title.contains(searchQuery, true) || it.url.contains(searchQuery, true) }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            )
            .shadow(16.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .padding(top = 16.dp, bottom = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Horizontal Carousel of Open Tab Cards (Screen 3)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredTabs, key = { it.id }) { tab ->
                    TabCarouselCard(
                        tab = tab,
                        isActive = tab.id == activeTabId,
                        onSelect = { onTabSelect(tab.id) },
                        onClose = { onCloseTab(tab.id) }
                    )
                }

                // Add New Tab Card
                item {
                    Box(
                        modifier = Modifier
                            .size(width = 150.dp, height = 210.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onNewTab(false) }
                            .testTag("new_tab_carousel_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "New Tab",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "New Tab",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The `🔍 Search tabs` Capsule (Screen 3)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(46.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search tabs",
                        tint = UberGray400,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.weight(1f)
                    )

                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search tabs",
                            color = UberGray400,
                            fontSize = 14.sp
                        )
                    } else {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = UberGray400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabCarouselCard(
    tab: BrowserTab,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(width = 175.dp, height = 220.dp)
            .shadow(if (isActive) 10.dp else 4.dp, RoundedCornerShape(18.dp))
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onSelect() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab Header with Favicon + Title + Close Button (Screen 3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (tab.title.contains("Dine", true)) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(14.dp)
                        )
                    } else if (tab.title.contains("Twitter", true)) {
                        Text(
                            text = "𝕏",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF276EF1))
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = tab.title.ifEmpty { "New Tab" },
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = UberGray400,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Tab Live Web Preview Mockup Body (Screen 3)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        if (tab.title.contains("Dine", true)) Color(0xFF243B55)
                        else if (tab.title.contains("Twitter", true)) Color(0xFF14171A)
                        else MaterialTheme.colorScheme.surface
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (tab.title.contains("Dine", true) || tab.url.contains("dine", true)) {
                    // Miniature representation of "Autumn '23 Collection"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Autumn ",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "'23",
                                color = MockupAutumnLime,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = "Collection",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xCC000000))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Shop Now",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (tab.title.contains("Twitter", true) || tab.url.contains("twitter", true)) {
                    // Miniature representation of Twitter profile
                    Column(
                        modifier = Modifier.padding(4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Ionut Zamfir ✓",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "@ionuss • Designer",
                            color = Color(0xFFAAAAAA),
                            fontSize = 9.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Product Designer who loves to create interfaces.",
                            color = Color.White,
                            fontSize = 9.sp,
                            maxLines = 2,
                            lineHeight = 11.sp
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = tab.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = tab.url.removePrefix("https://").substringBefore("/"),
                            color = UberGray400,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
