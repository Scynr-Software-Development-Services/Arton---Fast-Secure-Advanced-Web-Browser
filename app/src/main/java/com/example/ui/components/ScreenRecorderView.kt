package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BugReport
import com.example.recorder.SessionRecorderManager
import com.example.ui.theme.UberGray400
import com.example.ui.theme.UberGray600
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
fun FloatingRecorderBanner(
    recorder: SessionRecorderManager,
    onStopClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRecording by recorder.isRecording.collectAsState()
    val isPaused by recorder.isPaused.collectAsState()
    val elapsedSeconds by recorder.elapsedSeconds.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink"
    )

    AnimatedVisibility(
        visible = isRecording,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(UberPureBlack)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 7.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Record indicator dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .alpha(if (isPaused) 1f else blinkAlpha)
                        .clip(CircleShape)
                        .background(if (isPaused) UberYellow else UberRed)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isPaused) "PAUSED ${recorder.formatDuration(elapsedSeconds)}"
                    else "REC ${recorder.formatDuration(elapsedSeconds)}",
                    color = UberPureWhite,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Pause / Resume
                IconButton(
                    onClick = { recorder.togglePause() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = UberPureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Stop & Finish
                IconButton(
                    onClick = onStopClicked,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(UberRed)
                        .testTag("stop_recording_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = UberPureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenRecorderDialog(
    recorder: SessionRecorderManager,
    activeUrl: String,
    consoleErrorsCount: Int,
    onStartRecording: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isRecording by recorder.isRecording.collectAsState()
    val latestReport by recorder.latestReport.collectAsState()
    var userNotes by remember { mutableStateOf("") }
    var copiedReport by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Session Recorder",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Record workflow tutorials & bug sessions",
                        color = UberGray400,
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!isRecording && latestReport == null) {
                // Setup / Start Mode in Uber Card Style
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Record Browsing Workflow",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Arton captures page navigations, error diagnostics, timestamps, and workflow steps for tutorials or bug reporting.",
                            color = UberGray400,
                            fontSize = 13.5.sp,
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Target: ${activeUrl.substringBefore("?")}",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Uber Pill Action Button
                        Button(
                            onClick = {
                                onStartRecording()
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("start_recording_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSurface,
                                contentColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FiberManualRecord,
                                contentDescription = null,
                                tint = UberRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start Session Recording",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            } else if (isRecording) {
                // Currently Recording Active Card
                val elapsed by recorder.elapsedSeconds.collectAsState()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, UberRed.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "RECORDING IN PROGRESS",
                            color = UberRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = recorder.formatDuration(elapsed),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Browse websites normally. Stop anytime to review.",
                            color = UberGray400,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                recorder.stopRecording(activeUrl, consoleErrorsCount, userNotes)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UberRed,
                                contentColor = UberPureWhite
                            ),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Text("Stop & Review Report", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            } else if (latestReport != null) {
                // Report Generated Mode
                val report = latestReport!!
                val frames by recorder.recordedFrames.collectAsState()

                Column(modifier = Modifier.weight(1f)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Report Summary",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${frames.size} Steps",
                                    color = UberGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Duration: ${recorder.formatDuration(report.durationSeconds)} • Console Errors: ${report.consoleErrorsCount}",
                                color = UberGray400,
                                fontSize = 12.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "WORKFLOW STEPS",
                        color = UberGray400,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(frames) { frame ->
                            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(frame.timestamp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "[$time]",
                                    color = UberGray400,
                                    fontSize = 11.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = frame.note,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.5.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = userNotes,
                        onValueChange = { userNotes = it },
                        label = { Text("Add notes or tutorial summary") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        singleLine = false,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val fullSummary = recorder.generateShareableSummary(
                                    report.copy(userNotes = userNotes.ifEmpty { report.userNotes })
                                )
                                clipboardManager.setText(AnnotatedString(fullSummary))
                                copiedReport = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (copiedReport) UberGreen else MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(
                                text = if (copiedReport) "Copied!" else "Copy Report",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = {
                                val fullSummary = recorder.generateShareableSummary(
                                    report.copy(userNotes = userNotes.ifEmpty { report.userNotes })
                                )
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, fullSummary)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Report"))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onSurface,
                                contentColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
