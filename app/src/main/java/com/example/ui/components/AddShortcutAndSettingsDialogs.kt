package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.UberGray400
import com.example.ui.theme.UberGreen
import com.example.ui.theme.UberRed

@Composable
fun AddShortcutDialog(
    onAdd: (title: String, url: String, colorHex: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#000000") }

    val colors = listOf("#000000", "#FFFFFF", "#276EF1", "#05944F", "#FFC043", "#E11900")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(UberGray400)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Shortcut",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("e.g. GitHub") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shortcut_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Website URL") },
                    placeholder = { Text("e.g. github.com") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shortcut_url_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ACCENT COLOR",
                    color = UberGray400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    colors.forEach { hex ->
                        val parsed = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(parsed)
                                .border(
                                    width = if (selectedColor == hex) 3.dp else 1.dp,
                                    color = if (selectedColor == hex) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && url.isNotBlank()) {
                            onAdd(title, url, selectedColor)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_shortcut_btn"),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text("Save Shortcut", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    searchEngine: String,
    isDarkTheme: Boolean,
    isAdBlockEnabled: Boolean,
    isPowerSaverEnabled: Boolean,
    isForceDarkWebEnabled: Boolean,
    onSetSearchEngine: (String) -> Unit,
    onToggleDarkTheme: () -> Unit,
    onToggleAdBlock: () -> Unit,
    onTogglePowerSaver: () -> Unit,
    onToggleForceDarkWeb: () -> Unit,
    onClearAllData: () -> Unit,
    onDismiss: () -> Unit
) {
    var expandedEngine by remember { mutableStateOf(false) }
    val engines = listOf("Google", "DuckDuckGo", "Bing", "Brave", "Ecosia")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(UberGray400)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.4).sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "SEARCH ENGINE",
                    color = UberGray400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedEngine,
                    onExpandedChange = { expandedEngine = !expandedEngine }
                ) {
                    OutlinedTextField(
                        value = searchEngine,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEngine) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedEngine,
                        onDismissRequest = { expandedEngine = false }
                    ) {
                        engines.forEach { eng ->
                            DropdownMenuItem(
                                text = { Text(eng, fontWeight = FontWeight.Medium) },
                                onClick = {
                                    onSetSearchEngine(eng)
                                    expandedEngine = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Low Battery Usage Mode Setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Low Battery Mode",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Suspends background tabs & saves power",
                            color = UberGray400,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = isPowerSaverEnabled,
                        onCheckedChange = { onTogglePowerSaver() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = UberGreen,
                            checkedTrackColor = UberGreen.copy(alpha = 0.35f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dark Theme Setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dark Mode Interface",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Monochrome high-contrast design",
                            color = UberGray400,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleDarkTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onSurface,
                            checkedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Ad Blocker Setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ad & Tracker Shield",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Blocks invasive ads and web trackers",
                            color = UberGray400,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = isAdBlockEnabled,
                        onCheckedChange = { onToggleAdBlock() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = UberGreen,
                            checkedTrackColor = UberGreen.copy(alpha = 0.35f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                OutlinedButton(
                    onClick = onClearAllData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = UberRed)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clear Browsing History & Data", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
