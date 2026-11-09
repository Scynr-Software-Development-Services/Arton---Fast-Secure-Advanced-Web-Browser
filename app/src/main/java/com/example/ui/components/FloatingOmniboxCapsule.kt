package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrowserTab
import com.example.ui.theme.MockupLightningBlue
import com.example.ui.theme.MockupSubtleGray
import com.example.ui.theme.MockupTextGray
import com.example.ui.theme.UberGray400
import com.example.ui.theme.UberGreen
import com.example.ui.theme.UberPureBlack
import com.example.ui.theme.UberPureWhite

@Composable
fun FloatingOmniboxCapsule(
    tab: BrowserTab?,
    isAdBlockEnabled: Boolean,
    showAdBlockPopover: Boolean,
    onToggleAdBlockPopover: () -> Unit,
    onToggleAdBlockEnabled: () -> Unit,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onUrlSubmit: (String) -> Unit,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var textValue by remember(tab?.url) {
        val display = if (tab?.url == null || tab.url == "about:home" || tab.url == "about:blank") {
            ""
        } else {
            tab.url.removePrefix("https://").removePrefix("http://").removePrefix("www.")
        }
        mutableStateOf(display)
    }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val displayDomain = remember(tab?.url) {
        if (tab?.url == null || tab.url == "about:home" || tab.url == "about:blank") {
            "search or enter website"
        } else {
            tab.url.removePrefix("https://").removePrefix("http://").removePrefix("www.").substringBefore("/")
        }
    }

    var showAdvancedSettings by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // SCREEN 2: The Ad-Blocker Popup Card (renders smoothly right above the capsule)
        AnimatedVisibility(
            visible = showAdBlockPopover,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = Color(0x33000000)),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Bar mimicking capsule header inside popover as shown in image
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            enabled = tab?.canGoBack == true,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                contentDescription = "Back",
                                tint = if (tab?.canGoBack == true) MaterialTheme.colorScheme.onSurface else UberGray400,
                                modifier = Modifier.size(13.dp)
                            )
                        }

                        IconButton(
                            onClick = onForwardClick,
                            enabled = tab?.canGoForward == true,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Forward",
                                tint = if (tab?.canGoForward == true) MaterialTheme.colorScheme.onSurface else UberGray400,
                                modifier = Modifier.size(13.dp)
                            )
                        }

                        Text(
                            text = displayDomain,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onMicClick() }
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        // Active Blue Lightning Bolt Badge
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MockupLightningBlue)
                                .clickable { onToggleAdBlockPopover() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Ad Filter Active",
                                tint = UberPureWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Main Ad-Block Card Body (Screen 2)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Circular Power Toggle Button
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isAdBlockEnabled) UberPureBlack else UberGray400)
                                    .clickable { onToggleAdBlockEnabled() }
                                    .testTag("adblock_power_toggle"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PowerSettingsNew,
                                    contentDescription = "Toggle Ad Blocker",
                                    tint = UberPureWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Large Bold Number (e.g. 27)
                            val blocked = tab?.blockedAdsCount ?: 27
                            Text(
                                text = "$blocked",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-1).sp
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // Text: "Ads blocked on this site"
                            Text(
                                text = "Ads blocked on this site",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 19.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subtext from mockup: "If this site appears broken, try turning off the Ad Filter."
                    Text(
                        text = "If this site appears broken, try turning off the Ad Filter.",
                        color = UberGray400,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Collapsible "Advanced settings ∨"
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showAdvancedSettings = !showAdvancedSettings }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Advanced settings",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (showAdvancedSettings) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (showAdvancedSettings) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "• Cosmetic Elements: Hidden\n• Telemetry & Analytics: Blocked\n• Crypto Mining: Filtered\n• Site Status: Shield Protected",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // SCREEN 1 & 2: The Floating Omnibox Capsule
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(12.dp, RoundedCornerShape(27.dp), spotColor = Color(0x28000000))
                .clip(RoundedCornerShape(27.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(27.dp)
                )
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Chevron `<`
                IconButton(
                    onClick = onBackClick,
                    enabled = tab?.canGoBack == true,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = "Back",
                        tint = if (tab?.canGoBack == true) MaterialTheme.colorScheme.onSurface else UberGray400,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Forward Chevron `>`
                IconButton(
                    onClick = onForwardClick,
                    enabled = tab?.canGoForward == true,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Forward",
                        tint = if (tab?.canGoForward == true) MaterialTheme.colorScheme.onSurface else UberGray400,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Centered Domain / Address Input
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isEditing = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (isEditing) {
                        BasicTextField(
                            value = textValue,
                            onValueChange = { textValue = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    focusManager.clearFocus()
                                    isEditing = false
                                    if (textValue.isNotBlank()) {
                                        onUrlSubmit(textValue)
                                    }
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onFocusChanged {
                                    if (!it.isFocused) isEditing = false
                                }
                                .testTag("floating_omnibox_input")
                        )
                    } else {
                        Text(
                            text = displayDomain,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Microphone icon `🎙`
                IconButton(
                    onClick = {
                        isEditing = true
                        onMicClick()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Search",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // The Signature `⚡` Lightning Squircle Badge Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (showAdBlockPopover) MockupLightningBlue else UberPureBlack)
                        .clickable { onToggleAdBlockPopover() }
                        .testTag("lightning_adblock_badge"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Ad Blocker Shield",
                        tint = UberPureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
