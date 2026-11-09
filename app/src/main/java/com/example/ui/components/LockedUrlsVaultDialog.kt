package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.LockedUrlItem
import com.example.ui.theme.UberGray400
import com.example.ui.theme.UberGreen

@Composable
fun LockedUrlsVaultDialog(
    lockedUrls: List<LockedUrlItem>,
    onAddLockedUrl: (pattern: String, label: String) -> Unit,
    onToggleLock: (LockedUrlItem) -> Unit,
    onDeleteLock: (LockedUrlItem) -> Unit,
    onDismiss: () -> Unit
) {
    var newPattern by remember { mutableStateOf("") }
    var newLabel by remember { mutableStateOf("") }

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
                // Drag Indicator Pill
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(UberGray400)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "URL Lock Vault",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = "Protect sensitive websites with biometrics",
                            color = UberGray400,
                            fontSize = 12.sp
                        )
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

                Spacer(modifier = Modifier.height(18.dp))

                // Input to add new locked URL
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newPattern,
                        onValueChange = { newPattern = it },
                        placeholder = { Text("e.g. facebook.com or bank.com", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("lock_url_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Button(
                        onClick = {
                            if (newPattern.isNotBlank()) {
                                onAddLockedUrl(newPattern, newLabel.ifEmpty { newPattern })
                                newPattern = ""
                                newLabel = ""
                            }
                        },
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("add_lock_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface,
                            contentColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "LOCKED WEBSITES (${lockedUrls.size})",
                    color = UberGray400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (lockedUrls.isEmpty()) {
                        item {
                            Text(
                                text = "No locked websites yet.\nAdd any website domain above to enforce biometric lock.",
                                color = UberGray400,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                    items(lockedUrls, key = { it.id }) { item ->
                        LockedUrlRow(
                            item = item,
                            onToggle = { onToggleLock(item) },
                            onDelete = { onDeleteLock(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LockedUrlRow(
    item: LockedUrlItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.patternOrDomain,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (item.isLocked) "Biometric Lock Active" else "Paused",
                color = if (item.isLocked) UberGreen else UberGray400,
                fontSize = 11.5.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = item.isLocked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = UberGreen,
                    checkedTrackColor = UberGreen.copy(alpha = 0.35f),
                    uncheckedThumbColor = UberGray400,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surface
                )
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = UberGray400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
