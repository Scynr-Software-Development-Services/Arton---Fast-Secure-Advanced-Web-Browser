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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.WhmcsUser
import com.example.ui.theme.UberBlue
import com.example.ui.theme.UberDarkBorder
import com.example.ui.theme.UberGray400
import com.example.ui.theme.UberGray600
import com.example.ui.theme.UberGreen
import com.example.ui.theme.UberPureBlack
import com.example.ui.theme.UberPureWhite
import com.example.ui.theme.UberRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WhmcsProfileDialog(
    user: WhmcsUser,
    isSyncing: Boolean,
    syncMessage: String?,
    bookmarksCount: Int,
    shortcutsCount: Int,
    onLogin: (email: String, pass: String) -> Unit,
    onRegister: (first: String, last: String, email: String, pass: String, phone: String) -> Unit,
    onSyncNow: () -> Unit,
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register

    // Login Form State
    var loginEmail by remember { mutableStateOf("") }
    var loginPass by remember { mutableStateOf("") }

    // Register Form State
    var regFirst by remember { mutableStateOf("") }
    var regLast by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPass by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Uber Top Drag Indicator Pill
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
                            text = "SCYNR Account",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = "account.scynr.xyz • Cloud Sync",
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

                Spacer(modifier = Modifier.height(16.dp))

                // Feedback status message
                if (!syncMessage.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = syncMessage,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (user.isLoggedIn) {
                    // Profile & Cloud Sync Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "${user.firstName} ${user.lastName}".trim().ifEmpty { "Member" },
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = user.email,
                                        color = UberGray400,
                                        fontSize = 12.5.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Client ID: ${user.clientId}", color = UberGray400, fontSize = 13.sp)
                                Text(text = user.status, color = UberGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Cross-Platform Sync: $bookmarksCount bookmarks, $shortcutsCount shortcuts",
                                color = UberGray400,
                                fontSize = 12.sp
                            )

                            if (user.lastSyncedTime > 0) {
                                val syncDate = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(user.lastSyncedTime))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Last synced: $syncDate",
                                    color = UberBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Uber Signature High-Contrast Black/White Action Button
                    Button(
                        onClick = onSyncNow,
                        enabled = !isSyncing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("whmcs_sync_button"),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface,
                            contentColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.surface,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Syncing with SCYNR...", fontSize = 14.sp)
                        } else {
                            Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sync Data Now", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = UberRed)
                    ) {
                        Text("Sign Out", fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    // Auth Tabs: Login vs Register
                    TabRow(
                        selectedTabIndex = selectedAuthTab,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedAuthTab]),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedAuthTab == 0,
                            onClick = { selectedAuthTab = 0 },
                            text = { Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                        )
                        Tab(
                            selected = selectedAuthTab == 1,
                            onClick = { selectedAuthTab = 1 },
                            text = { Text("Register", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    if (selectedAuthTab == 0) {
                        // Login Mode
                        OutlinedTextField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = loginPass,
                            onValueChange = { loginPass = it },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (loginEmail.isNotBlank() && loginPass.isNotBlank()) {
                                    onLogin(loginEmail, loginPass)
                                }
                            },
                            enabled = !isSyncing,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_btn"),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSurface,
                                contentColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Continue with SCYNR", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    } else {
                        // Register Mode
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = regFirst,
                                onValueChange = { regFirst = it },
                                label = { Text("First Name") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            OutlinedTextField(
                                value = regLast,
                                onValueChange = { regLast = it },
                                label = { Text("Last Name") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPass,
                            onValueChange = { regPass = it },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            label = { Text("Phone Number") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (regEmail.isNotBlank() && regPass.isNotBlank() && regFirst.isNotBlank()) {
                                    onRegister(regFirst, regLast, regEmail, regPass, regPhone)
                                }
                            },
                            enabled = !isSyncing,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("register_submit_btn"),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSurface,
                                contentColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
