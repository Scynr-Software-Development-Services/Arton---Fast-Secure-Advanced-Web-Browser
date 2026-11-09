package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DownloadItem
import com.example.ui.theme.UberBlue
import com.example.ui.theme.UberGreen
import com.example.ui.theme.UberPureBlack
import com.example.ui.theme.UberPureWhite

@Composable
fun DownloadsScreen(
    downloads: List<DownloadItem>,
    onStartDownload: (url: String, fileName: String, isTurbo: Boolean) -> Unit,
    onPauseDownload: (DownloadItem) -> Unit,
    onResumeDownload: (DownloadItem) -> Unit,
    onDeleteDownload: (DownloadItem) -> Unit,
    onClearAll: () -> Unit,
    onBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var isTurboEnabled by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newDownloadUrl by remember { mutableStateOf("") }

    val filteredList = remember(downloads, selectedFilter) {
        when (selectedFilter) {
            "Active" -> downloads.filter { it.status == "DOWNLOADING" || it.status == "PAUSED" }
            "Completed" -> downloads.filter { it.status == "COMPLETED" }
            "Torrents" -> downloads.filter { it.isTorrent }
            "Warnings" -> downloads.filter { !it.isSecure || it.status == "WARNING" }
            else -> downloads
        }
    }

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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            text = "Downloads",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${downloads.size} items • ${downloads.count { it.status == "DOWNLOADING" }} active",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }

                if (downloads.isNotEmpty()) {
                    IconButton(onClick = onClearAll) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear all",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = UberBlue,
                contentColor = UberPureWhite,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Download")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Turbo Multi-threading Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isTurboEnabled) Color(0xFF00E5FF).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Turbo",
                                tint = if (isTurboEnabled) Color(0xFF00E5FF) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Turbo Acceleration",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isTurboEnabled) "16 parallel chunks active • Torrent & HTTP" else "Standard single connection",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Switch(
                        checked = isTurboEnabled,
                        onCheckedChange = { isTurboEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = UberPureWhite,
                            checkedTrackColor = UberBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Pills Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Active", "Completed", "Torrents", "Warnings").forEach { filter ->
                    val isSelected = filter == selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.onBackground
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Downloads List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No downloads in this section",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        DownloadCard(
                            item = item,
                            onPause = { onPauseDownload(item) },
                            onResume = { onResumeDownload(item) },
                            onDelete = { onDeleteDownload(item) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Download or Torrent", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Paste a direct download URL (.iso, .zip, .pdf) or a BitTorrent magnet link (magnet:?xt=...)",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newDownloadUrl,
                        onValueChange = { newDownloadUrl = it },
                        placeholder = { Text("https://... or magnet:?xt=...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDownloadUrl.isNotBlank()) {
                            onStartDownload(newDownloadUrl, "", isTurboEnabled)
                            newDownloadUrl = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UberBlue)
                ) {
                    Text("Start", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DownloadCard(
    item: DownloadItem,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 1.dp,
                color = if (!item.isSecure) Color(0xFFFF5252).copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Icon + Title + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when {
                                    !item.isSecure -> Color(0xFFFF5252).copy(alpha = 0.15f)
                                    item.isTorrent -> Color(0xFF7C4DFF).copy(alpha = 0.15f)
                                    item.status == "COMPLETED" -> UberGreen.copy(alpha = 0.15f)
                                    else -> UberBlue.copy(alpha = 0.15f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                !item.isSecure -> Icons.Default.Warning
                                item.isTorrent -> Icons.Default.Hub
                                item.status == "COMPLETED" -> Icons.Default.CheckCircle
                                else -> Icons.Default.Download
                            },
                            contentDescription = null,
                            tint = when {
                                !item.isSecure -> Color(0xFFFF5252)
                                item.isTorrent -> Color(0xFF7C4DFF)
                                item.status == "COMPLETED" -> UberGreen
                                else -> UberBlue
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = item.fileName,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (item.isTorrent) {
                                Text(
                                    text = "TORRENT • ${item.torrentSeeds} seeds • ${item.torrentPeers} peers",
                                    color = Color(0xFF7C4DFF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    text = formatBytes(item.totalBytes),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Action button (Play/Pause or Delete)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.status == "DOWNLOADING") {
                        IconButton(onClick = onPause, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.Pause, contentDescription = "Pause", modifier = Modifier.size(18.dp))
                        }
                    } else if (item.status == "PAUSED") {
                        IconButton(onClick = onResume, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Resume", modifier = Modifier.size(18.dp))
                        }
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Security Warning Callout if potentially harmful
            if (!item.isSecure && item.securityWarning != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFF5252).copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.securityWarning,
                            color = Color(0xFFFF5252),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Progress Bar if still downloading
            if (item.status == "DOWNLOADING" || item.status == "PAUSED") {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { item.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (item.isTorrent) Color(0xFF7C4DFF) else UberBlue,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.progress}% • ${item.downloadSpeed}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${formatBytes(item.downloadedBytes)} / ${formatBytes(item.totalBytes)}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "Unknown"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
        gb >= 1.0 -> String.format("%.2f GB", gb)
        mb >= 1.0 -> String.format("%.1f MB", mb)
        kb >= 1.0 -> String.format("%.0f KB", kb)
        else -> "$bytes B"
    }
}
