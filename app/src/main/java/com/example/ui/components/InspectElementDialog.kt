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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrowserTab
import com.example.data.model.DevConsoleMessage
import com.example.ui.theme.UberBlue
import com.example.ui.theme.UberDarkBorder
import com.example.ui.theme.UberGray400
import com.example.ui.theme.UberGreen
import com.example.ui.theme.UberNearBlack
import com.example.ui.theme.UberPureBlack
import com.example.ui.theme.UberPureWhite
import com.example.ui.theme.UberRed
import com.example.ui.theme.UberYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InspectElementDialog(
    tab: BrowserTab?,
    domContent: String,
    consoleLogs: List<DevConsoleMessage>,
    onRunJs: (String) -> Unit,
    onClearConsole: () -> Unit,
    onRefreshDom: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val clipboardManager = LocalClipboardManager.current
    var jsInput by remember { mutableStateOf("") }
    var copiedFeedback by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Developer Tools",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tab?.url ?: "about:blank",
                            color = UberGray400,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("DOM / Elements", fontSize = 13.5.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Console", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                            if (consoleLogs.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${consoleLogs.size}",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Page Telemetry", fontSize = 13.5.sp, fontWeight = FontWeight.Bold) }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // DOM / Elements Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "HTML Document (${domContent.length} chars)",
                                color = UberGray400,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(domContent))
                                        copiedFeedback = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = if (copiedFeedback) UberGreen else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = onRefreshDom,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            if (domContent.isEmpty()) {
                                Text(
                                    text = "Loading DOM tree from active webview...\n(Open or navigate to any web page to inspect elements)",
                                    color = UberGray400,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            } else {
                                SelectionContainer {
                                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                        Text(
                                            text = domContent,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            lineHeight = 17.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Console Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "JavaScript Console Output",
                                color = UberGray400,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            IconButton(
                                onClick = onClearConsole,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = UberGray400,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (consoleLogs.isEmpty()) {
                                item {
                                    Text(
                                        text = "No console output recorded yet.\nRun JS commands below or browse web pages with logs.",
                                        color = UberGray400,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                            items(consoleLogs, key = { it.id }) { log ->
                                UberConsoleLogItem(log)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Interactive JS Execution Bar with Uber Pill Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = jsInput,
                                onValueChange = { jsInput = it },
                                placeholder = {
                                    Text(
                                        text = "e.g. document.title",
                                        color = UberGray400,
                                        fontSize = 12.5.sp
                                    )
                                },
                                leadingIcon = {
                                    Text(
                                        text = "> ",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("js_eval_input"),
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.5.sp
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    if (jsInput.isNotBlank()) {
                                        onRunJs(jsInput)
                                        jsInput = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurface)
                                    .testTag("run_js_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Execute",
                                    tint = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Page Telemetry Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(18.dp)
                    ) {
                        UberTelemetryCard("URL", tab?.url ?: "about:blank")
                        UberTelemetryCard("Page Title", tab?.title ?: "N/A")
                        UberTelemetryCard("Security Protocol", if (tab?.url?.startsWith("https") == true) "TLS / HTTPS (Encrypted)" else "Cleartext HTTP / Insecure")
                        UberTelemetryCard("Session Mode", if (tab?.isPrivate == true) "Private / Incognito (Isolated Cache)" else "Standard Browsing")
                        UberTelemetryCard("Desktop Mode", if (tab?.isDesktopMode == true) "Enabled (Linux x86_64 User Agent)" else "Disabled (Android Mobile)")
                        UberTelemetryCard("Ads Blocked This Page", "${tab?.blockedAdsCount ?: 0} ads & trackers prevented")
                        UberTelemetryCard("Reader Mode", if (tab?.isReaderMode == true) "Active (Distraction-Free)" else "Inactive")
                    }
                }
            }
        }
    }
}

@Composable
private fun UberConsoleLogItem(log: DevConsoleMessage) {
    val (levelColor, prefix) = when (log.level) {
        "error" -> UberRed to "ERR"
        "warn" -> UberYellow to "WRN"
        "info" -> UberBlue to "INF"
        else -> MaterialTheme.colorScheme.onSurface to "LOG"
    }

    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "[$timeStr] ",
            color = UberGray400,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(levelColor.copy(alpha = 0.2f))
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text(
                text = prefix,
                color = levelColor,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = log.message,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun UberTelemetryCard(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = UberGray400,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
