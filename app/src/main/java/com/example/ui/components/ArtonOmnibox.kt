package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BrowserTab
import com.example.ui.theme.UberBlue
import com.example.ui.theme.UberGray400
import com.example.ui.theme.UberGreen
import com.example.ui.theme.UberPureBlack
import com.example.ui.theme.UberPureWhite
import com.example.ui.theme.UberPurple

@Composable
fun ArtonOmnibox(
    tab: BrowserTab?,
    tabsCount: Int,
    isAdBlockEnabled: Boolean,
    onMenuClick: () -> Unit,
    onSearchOrUrlSubmit: (String) -> Unit,
    onReloadClick: () -> Unit,
    onTabSwitcherClick: () -> Unit,
    onShieldClick: () -> Unit,
    onReaderModeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textValue by remember(tab?.url) {
        val display = if (tab?.url == "about:home") "" else (tab?.url ?: "")
        mutableStateOf(display)
    }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val isHome = tab?.url == "about:home"
    val isHttps = tab?.url?.startsWith("https://") == true
    val isPrivate = tab?.isPrivate == true

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Uber-style Hamburger button
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .size(42.dp)
                    .testTag("menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Sidebar",
                    tint = if (isPrivate) UberPurple else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Uber-style Capsule Omnibox Bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 1.dp,
                        color = if (isFocused) {
                            if (isPrivate) UberPurple else MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        shape = RoundedCornerShape(22.dp)
                    )
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Security or Privacy Status
                    if (!isFocused) {
                        if (isPrivate) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(UberPurple.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "P",
                                    color = UberPurple,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else if (isHttps) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Secure Connection",
                                tint = UberGreen,
                                modifier = Modifier.size(15.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = "Website",
                                tint = UberGray400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Input Field
                    Box(modifier = Modifier.weight(1f)) {
                        BasicTextField(
                            value = textValue,
                            onValueChange = { textValue = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onFocusChanged { isFocused = it.isFocused }
                                .testTag("omnibox_input"),
                            singleLine = true,
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            cursorBrush = SolidColor(if (isPrivate) UberPurple else MaterialTheme.colorScheme.onSurface),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    focusManager.clearFocus()
                                    if (textValue.isNotBlank()) {
                                        onSearchOrUrlSubmit(textValue)
                                    }
                                }
                            )
                        )

                        if (textValue.isEmpty() && !isFocused) {
                            Text(
                                text = if (isPrivate) "Search anonymously..." else "Search or type URL...",
                                color = UberGray400,
                                fontSize = 13.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Clear button
                    if (isFocused && textValue.isNotEmpty()) {
                        IconButton(
                            onClick = { textValue = "" },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = UberGray400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Shield badge
                    if (isAdBlockEnabled && !isHome) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onShieldClick() }
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Shield",
                                    tint = UberGreen,
                                    modifier = Modifier.size(15.dp)
                                )
                                val blocked = tab?.blockedAdsCount ?: 0
                                if (blocked > 0) {
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "$blocked",
                                        color = UberGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Reader Mode
                    if (!isHome) {
                        IconButton(
                            onClick = onReaderModeClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AutoStories,
                                contentDescription = "Reader Mode",
                                tint = if (tab?.isReaderMode == true) UberBlue else UberGray400,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    // Reload
                    if (!isHome && !isFocused) {
                        IconButton(
                            onClick = onReloadClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reload",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Uber-style Tab Counter Pill
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onTabSwitcherClick() }
                    .border(
                        width = 1.dp,
                        color = if (isPrivate) UberPurple else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .testTag("tab_switcher_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$tabsCount",
                    color = if (isPrivate) UberPurple else MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Web Loading Progress Bar in Uber Blue
        val isLoading = tab?.isLoading == true
        val progress = (tab?.progress ?: 0) / 100f
        AnimatedVisibility(
            visible = isLoading && progress < 1f,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = if (isPrivate) UberPurple else UberBlue,
                trackColor = Color.Transparent,
            )
        }
    }
}
