package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.BrowserScreenState
import com.example.ui.components.AddShortcutDialog
import com.example.ui.components.ArtonBottomBar
import com.example.ui.components.ArtonDrawer
import com.example.ui.components.ArtonWebView
import com.example.ui.components.BiometricLockDialog
import com.example.ui.components.BookmarksDialog
import com.example.ui.components.DineInStyleHeroView
import com.example.ui.components.FloatingOmniboxCapsule
import com.example.ui.components.FloatingRecorderBanner
import com.example.ui.components.InspectElementDialog
import com.example.ui.components.ScreenRecorderDialog
import com.example.ui.components.TabSheetView
import com.example.ui.theme.UberBlue
import com.example.ui.theme.UberGray400
import com.example.viewmodel.BrowserViewModel
import kotlinx.coroutines.launch

@Composable
fun MainBrowserScreen(viewModel: BrowserViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentScreenState by viewModel.currentScreenState.collectAsState()
    val downloads by viewModel.downloads.collectAsState()
    val currentWallpaper by viewModel.currentWallpaper.collectAsState()
    val widgetConfig by viewModel.widgetConfig.collectAsState()

    val isSplashVisible by viewModel.isSplashVisible.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val searchEngine by viewModel.searchEngine.collectAsState()
    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val shortcuts by viewModel.shortcuts.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val history by viewModel.history.collectAsState()
    val lockedUrls by viewModel.lockedUrls.collectAsState()

    val isAdBlockEnabled by viewModel.isAdBlockEnabled.collectAsState()
    val isPowerSaverEnabled by viewModel.isPowerSaverEnabled.collectAsState()
    val isForceDarkWebEnabled by viewModel.isForceDarkWebEnabled.collectAsState()
    val adsBlockedCount by viewModel.totalAdsBlocked.collectAsState()
    val trackersBlockedCount by viewModel.totalTrackersBlocked.collectAsState()
    val dataSavedKb by viewModel.dataSavedKb.collectAsState()

    val whmcsUser by viewModel.whmcsUser.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()

    // Trigger & Popover collectors
    val showAdBlockSheet by viewModel.showAdBlockSheet.collectAsState()
    val showTabSwitcher by viewModel.showTabSwitcher.collectAsState()
    val webBackTrigger by viewModel.webBackTrigger.collectAsState()
    val webForwardTrigger by viewModel.webForwardTrigger.collectAsState()

    // Dialog state collectors
    val showInspectElement by viewModel.showInspectElement.collectAsState()
    val showBookmarksDialog by viewModel.showBookmarksDialog.collectAsState()
    val showRecorderDialog by viewModel.showRecorderDialog.collectAsState()
    val showAddShortcutDialog by viewModel.showAddShortcutDialog.collectAsState()
    val showSecurityAuthPrompt by viewModel.showSecurityAuthPrompt.collectAsState()
    val securityReason by viewModel.securityPromptReason.collectAsState()

    val domContent by viewModel.domContent.collectAsState()
    val consoleLogs by viewModel.consoleLogs.collectAsState()

    // Intercept Back Press gracefully
    BackHandler(
        enabled = currentScreenState != BrowserScreenState.BROWSER_MAIN ||
                drawerState.isOpen || showAdBlockSheet || showTabSwitcher ||
                showInspectElement || showBookmarksDialog || showRecorderDialog ||
                (activeTab != null && activeTab?.url != "about:home")
    ) {
        when {
            currentScreenState != BrowserScreenState.BROWSER_MAIN -> {
                viewModel.navigateBack()
            }
            drawerState.isOpen -> scope.launch { drawerState.close() }
            showAdBlockSheet -> viewModel.showAdBlockSheet.value = false
            showTabSwitcher -> viewModel.showTabSwitcher.value = false
            showInspectElement -> viewModel.showInspectElement.value = false
            showBookmarksDialog -> viewModel.showBookmarksDialog.value = false
            showRecorderDialog -> viewModel.showRecorderDialog.value = false
            activeTab?.canGoBack == true -> viewModel.triggerWebBack()
            activeTab?.url != "about:home" -> {
                viewModel.updateTabUrl(activeTabId, "about:home", "Home")
            }
        }
    }

    // Root Screen Routing: If user opened a dedicated screen, render it with smooth slide
    AnimatedContent(
        targetState = currentScreenState,
        transitionSpec = {
            if (targetState != BrowserScreenState.BROWSER_MAIN) {
                slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
            } else {
                slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
            }
        },
        label = "screen_routing"
    ) { screen ->
        when (screen) {
            BrowserScreenState.DOWNLOADS -> {
                DownloadsScreen(
                    downloads = downloads,
                    onStartDownload = { url, name, turbo -> viewModel.startDownload(url, name, turbo) },
                    onPauseDownload = { viewModel.pauseDownload(it) },
                    onResumeDownload = { viewModel.resumeDownload(it) },
                    onDeleteDownload = { viewModel.deleteDownload(it) },
                    onClearAll = { viewModel.clearDownloads() },
                    onBack = { viewModel.navigateBack() }
                )
            }
            BrowserScreenState.SCYNR_AUTH -> {
                ScynrAuthScreen(
                    user = whmcsUser,
                    isSyncing = isSyncing,
                    syncMessage = syncMessage ?: "",
                    onLogin = { e, p -> viewModel.loginWhmcs(e, p) },
                    onRegister = { f, l, e, p, ph -> viewModel.registerWhmcs(f, l, e, p, ph) },
                    onBack = { viewModel.navigateBack() }
                )
            }
            BrowserScreenState.ACCOUNT_PROFILE -> {
                AccountProfileScreen(
                    user = whmcsUser,
                    isSyncing = isSyncing,
                    syncMessage = syncMessage ?: "",
                    bookmarksCount = bookmarks.size,
                    shortcutsCount = shortcuts.size,
                    onSyncNow = { viewModel.syncWithWhmcs() },
                    onNavigateToAuth = { viewModel.navigateTo(BrowserScreenState.SCYNR_AUTH) },
                    onLogout = { viewModel.logoutWhmcs() },
                    onBack = { viewModel.navigateBack() }
                )
            }
            BrowserScreenState.HISTORY -> {
                HistoryScreen(
                    history = history,
                    onSelectHistory = { url ->
                        if (!viewModel.checkUrlLock(url)) {
                            viewModel.updateTabUrl(activeTabId, url, "Loading...")
                            viewModel.navigateBack()
                        }
                    },
                    onDeleteHistoryItem = { viewModel.deleteHistoryItem(it) },
                    onClearAll = { viewModel.clearAllHistory() },
                    onBack = { viewModel.navigateBack() }
                )
            }
            BrowserScreenState.SETTINGS -> {
                SettingsScreen(
                    searchEngine = searchEngine,
                    isDarkTheme = isDarkTheme,
                    isAdBlockEnabled = isAdBlockEnabled,
                    isPowerSaverEnabled = isPowerSaverEnabled,
                    isForceDarkWebEnabled = isForceDarkWebEnabled,
                    currentWallpaper = currentWallpaper,
                    widgetConfig = widgetConfig,
                    onSetSearchEngine = { viewModel.setSearchEngine(it) },
                    onToggleDarkTheme = { viewModel.toggleTheme() },
                    onToggleAdBlock = { viewModel.toggleAdBlock() },
                    onTogglePowerSaver = { viewModel.togglePowerSaver() },
                    onToggleForceDarkWeb = { viewModel.toggleForceDarkWeb() },
                    onSetWallpaper = { viewModel.setWallpaper(it) },
                    onUpdateWidgetConfig = { viewModel.updateWidgetConfig(it) },
                    onClearAllData = {
                        viewModel.clearAllHistory()
                        scope.launch { snackbarHostState.showSnackbar("History and browsing data cleared.") }
                    },
                    onBack = { viewModel.navigateBack() }
                )
            }
            BrowserScreenState.URL_LOCK_VAULT -> {
                UrlLockVaultScreen(
                    lockedUrls = lockedUrls,
                    onAddLockedUrl = { pattern, label -> viewModel.addLockedUrl(pattern, label) },
                    onToggleLock = { viewModel.toggleLockedUrl(it) },
                    onDeleteLock = { viewModel.deleteLockedUrl(it) },
                    onBack = { viewModel.navigateBack() }
                )
            }
            BrowserScreenState.BROWSER_MAIN -> {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ArtonDrawer(
                            whmcsUser = whmcsUser,
                            tabsCount = tabs.size,
                            isDarkTheme = isDarkTheme,
                            isAdBlockEnabled = isAdBlockEnabled,
                            isPowerSaverEnabled = isPowerSaverEnabled,
                            isForceDarkWebEnabled = isForceDarkWebEnabled,
                            onNavigateHome = {
                                scope.launch { drawerState.close() }
                                viewModel.updateTabUrl(activeTabId, "about:home", "Home")
                            },
                            onNewTab = { isPrivate ->
                                scope.launch { drawerState.close() }
                                viewModel.openNewTab(isPrivate = isPrivate)
                            },
                            onShowTabs = {
                                scope.launch { drawerState.close() }
                                viewModel.showTabSwitcher.value = true
                            },
                            onShowBookmarks = {
                                scope.launch { drawerState.close() }
                                viewModel.showBookmarksDialog.value = true
                            },
                            onShowHistory = {
                                scope.launch { drawerState.close() }
                                viewModel.navigateTo(BrowserScreenState.HISTORY)
                            },
                            onShowVault = {
                                scope.launch { drawerState.close() }
                                viewModel.navigateTo(BrowserScreenState.URL_LOCK_VAULT)
                            },
                            onShowDevTools = {
                                scope.launch { drawerState.close() }
                                viewModel.showInspectElement.value = true
                            },
                            onShowRecorder = {
                                scope.launch { drawerState.close() }
                                viewModel.showRecorderDialog.value = true
                            },
                            onShowDownloads = {
                                scope.launch { drawerState.close() }
                                viewModel.navigateTo(BrowserScreenState.DOWNLOADS)
                            },
                            onShowWhmcsProfile = {
                                scope.launch { drawerState.close() }
                                viewModel.navigateTo(BrowserScreenState.ACCOUNT_PROFILE)
                            },
                            onShowSettings = {
                                scope.launch { drawerState.close() }
                                viewModel.navigateTo(BrowserScreenState.SETTINGS)
                            },
                            onToggleTheme = { viewModel.toggleTheme() },
                            onToggleAdBlock = { viewModel.toggleAdBlock() },
                            onTogglePowerSaver = { viewModel.togglePowerSaver() },
                            onToggleForceDarkWeb = { viewModel.toggleForceDarkWeb() }
                        )
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                            // Bottom Area: TabSheet (when open) + Floating Omnibox (Screens 1 & 2) + ArtonBottomBar
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                            ) {
                                // 1. Sliding Tab Switcher Carousel Sheet (Screen 3)
                                AnimatedVisibility(
                                    visible = showTabSwitcher,
                                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                                ) {
                                    TabSheetView(
                                        tabs = tabs,
                                        activeTabId = activeTabId,
                                        onTabSelect = { id ->
                                            viewModel.switchTab(id)
                                            viewModel.showTabSwitcher.value = false
                                        },
                                        onCloseTab = { id -> viewModel.closeTab(id) },
                                        onNewTab = { isPrivate ->
                                            viewModel.openNewTab(isPrivate = isPrivate)
                                            viewModel.showTabSwitcher.value = false
                                        },
                                        onDismiss = { viewModel.showTabSwitcher.value = false }
                                    )
                                }

                                // 2. Floating Omnibox Capsule (Screens 1 & 2)
                                AnimatedVisibility(
                                    visible = !showTabSwitcher,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    FloatingOmniboxCapsule(
                                        tab = activeTab,
                                        isAdBlockEnabled = isAdBlockEnabled,
                                        showAdBlockPopover = showAdBlockSheet,
                                        onToggleAdBlockPopover = { viewModel.toggleAdBlockSheet() },
                                        onToggleAdBlockEnabled = { viewModel.toggleAdBlock() },
                                        onBackClick = { viewModel.triggerWebBack() },
                                        onForwardClick = { viewModel.triggerWebForward() },
                                        onUrlSubmit = { query ->
                                            val resolved = viewModel.resolveSearchOrUrl(query)
                                            if (!viewModel.checkUrlLock(resolved)) {
                                                viewModel.updateTabUrl(activeTabId, resolved, "Loading...")
                                            }
                                        },
                                        onMicClick = {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Voice search active: Speak a website or search terms...")
                                            }
                                        },
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )
                                }

                                // 3. 5-Item Arton Bottom Navigation Bar
                                ArtonBottomBar(
                                    tabsCount = tabs.size,
                                    isTabSheetOpen = showTabSwitcher,
                                    isPrivate = activeTab?.isPrivate == true,
                                    onHistoryClick = {
                                        if (activeTab?.isPrivate == true) {
                                            viewModel.navigateTo(BrowserScreenState.URL_LOCK_VAULT)
                                        } else {
                                            viewModel.navigateTo(BrowserScreenState.HISTORY)
                                        }
                                    },
                                    onBookmarksClick = {
                                        viewModel.showBookmarksDialog.value = true
                                    },
                                    onCenterOrbClick = {
                                        viewModel.updateTabUrl(activeTabId, "about:home", "Arton Home")
                                    },
                                    onTabCounterClick = {
                                        viewModel.toggleTabSwitcher()
                                    },
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            val currentTab = activeTab
                            if (currentTab == null || currentTab.url == "about:home" || currentTab.url == "about:blank") {
                                BrowserDashboard(
                                    shortcuts = shortcuts,
                                    isPrivate = currentTab?.isPrivate == true,
                                    searchEngine = searchEngine,
                                    adsBlockedCount = adsBlockedCount,
                                    trackersBlockedCount = trackersBlockedCount,
                                    dataSavedKb = dataSavedKb,
                                    isPowerSaverActive = isPowerSaverEnabled,
                                    currentWallpaper = currentWallpaper,
                                    widgetConfig = widgetConfig,
                                    onSearchClick = {},
                                    onShortcutClick = { shortcut ->
                                        if (!viewModel.checkUrlLock(shortcut.url)) {
                                            viewModel.updateTabUrl(activeTabId, shortcut.url, shortcut.title)
                                        }
                                    },
                                    onDeleteShortcut = { shortcut -> viewModel.deleteShortcut(shortcut) },
                                    onAddShortcutClick = { viewModel.showAddShortcutDialog.value = true },
                                    onTogglePrivateMode = {
                                        viewModel.openNewTab(isPrivate = !(currentTab?.isPrivate ?: false))
                                    },
                                    onOpenDevTools = { viewModel.showInspectElement.value = true },
                                    onOpenVault = { viewModel.navigateTo(BrowserScreenState.URL_LOCK_VAULT) },
                                    onOpenRecorder = { viewModel.showRecorderDialog.value = true },
                                    onOpenDownloads = { viewModel.navigateTo(BrowserScreenState.DOWNLOADS) },
                                    onOpenAccount = { viewModel.navigateTo(BrowserScreenState.ACCOUNT_PROFILE) },
                                    onSetWallpaper = { viewModel.setWallpaper(it) },
                                    onUpdateWidgetConfig = { viewModel.updateWidgetConfig(it) }
                                )
                            } else if (currentTab.url.contains("dineinstyle.com", ignoreCase = true)) {
                                // Match Screen 1 & Screen 2: Autumn '23 Collection
                                DineInStyleHeroView(
                                    onShopNowClick = {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Autumn '23 Collection: 24 Unique Pieces loaded.")
                                        }
                                    }
                                )
                            } else {
                                ArtonWebView(
                                    tab = currentTab,
                                    isAdBlockEnabled = isAdBlockEnabled,
                                    isPowerSaverEnabled = isPowerSaverEnabled,
                                    isForceDarkWebEnabled = isForceDarkWebEnabled,
                                    backTrigger = webBackTrigger,
                                    forwardTrigger = webForwardTrigger,
                                    onUrlChanged = { url, title, canBack, canFwd ->
                                        viewModel.updateTabUrl(currentTab.id, url, title, canBack, canFwd)
                                    },
                                    onProgressChanged = { progress, isLoading ->
                                        viewModel.updateTabProgress(currentTab.id, progress, isLoading)
                                    },
                                    onAdBlocked = {
                                        viewModel.incrementTabBlockedAds(currentTab.id)
                                    },
                                    onCheckUrlLock = { targetUrl ->
                                        viewModel.checkUrlLock(targetUrl)
                                    },
                                    onConsoleLog = { msg ->
                                        viewModel.addConsoleLog(msg)
                                    },
                                    onDomExtracted = { html ->
                                        viewModel.setDomContent(html)
                                    }
                                )
                            }

                            // Floating Recording Pill on Top if active
                            FloatingRecorderBanner(
                                recorder = viewModel.recorderManager,
                                onStopClicked = {
                                    viewModel.recorderManager.stopRecording(
                                        activeTab?.url ?: "about:home",
                                        consoleLogs.count { it.level == "error" }
                                    )
                                    viewModel.showRecorderDialog.value = true
                                },
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .statusBarsPadding()
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Overlays for Tools & Security
    if (showInspectElement) {
        InspectElementDialog(
            tab = activeTab,
            domContent = domContent,
            consoleLogs = consoleLogs,
            onRunJs = { js ->
                viewModel.addConsoleLog(
                    com.example.data.model.DevConsoleMessage(
                        level = "log",
                        message = "> $js"
                    )
                )
            },
            onClearConsole = { viewModel.clearConsoleLogs() },
            onRefreshDom = {},
            onDismiss = { viewModel.showInspectElement.value = false }
        )
    }

    if (showRecorderDialog) {
        ScreenRecorderDialog(
            recorder = viewModel.recorderManager,
            activeUrl = activeTab?.url ?: "about:home",
            consoleErrorsCount = consoleLogs.count { it.level == "error" },
            onStartRecording = {
                viewModel.recorderManager.startRecording(activeTab?.url ?: "about:home")
            },
            onDismiss = { viewModel.showRecorderDialog.value = false }
        )
    }

    if (showBookmarksDialog) {
        BookmarksDialog(
            bookmarks = bookmarks,
            onSelectBookmark = { url ->
                if (!viewModel.checkUrlLock(url)) {
                    viewModel.updateTabUrl(activeTabId, url, "Loading...")
                }
            },
            onDeleteBookmark = { b -> viewModel.deleteBookmark(b) },
            onDismiss = { viewModel.showBookmarksDialog.value = false }
        )
    }

    if (showAddShortcutDialog) {
        AddShortcutDialog(
            onAdd = { title, url, colorHex ->
                viewModel.addShortcut(title, url, colorHex)
            },
            onDismiss = { viewModel.showAddShortcutDialog.value = false }
        )
    }

    if (showSecurityAuthPrompt) {
        BiometricLockDialog(
            reason = securityReason,
            onSuccess = { viewModel.onSecurityAuthSuccess() },
            onDismiss = { viewModel.onSecurityAuthDismissed() }
        )
    }

    // Modern Animated Splash Screen on App Launch
    AnimatedVisibility(
        visible = isSplashVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        SplashScreen(onDismiss = { viewModel.dismissSplash() })
    }
}
