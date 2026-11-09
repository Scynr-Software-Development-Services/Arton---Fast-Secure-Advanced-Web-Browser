package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.adblock.AdBlockEngine
import com.example.data.local.BrowserDatabase
import com.example.data.download.DownloadEngine
import com.example.data.model.BookmarkItem
import com.example.data.model.BrowserScreenState
import com.example.data.model.BrowserTab
import com.example.data.model.DevConsoleMessage
import com.example.data.model.DownloadItem
import com.example.data.model.HistoryItem
import com.example.data.model.LockedUrlItem
import com.example.data.model.SettingEntity
import com.example.data.model.ShortcutItem
import com.example.data.model.WallpaperType
import com.example.data.model.WhmcsUser
import com.example.data.model.WidgetPersonalization
import com.example.data.whmcs.WhmcsClient
import com.example.recorder.SessionRecorderManager
import com.example.security.BiometricAuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BrowserDatabase.getDatabase(application)
    private val dao = db.browserDao()

    val recorderManager = SessionRecorderManager()

    // Splash screen state
    private val _isSplashVisible = MutableStateFlow(true)
    val isSplashVisible: StateFlow<Boolean> = _isSplashVisible.asStateFlow()

    // Active Theme
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Search Engine
    private val _searchEngine = MutableStateFlow("Google")
    val searchEngine: StateFlow<String> = _searchEngine.asStateFlow()

    // Tabs matching mockup
    private val _tabs = MutableStateFlow<List<BrowserTab>>(
        listOf(
            BrowserTab(
                id = "tab-1",
                url = "https://dineinstyle.com",
                title = "Dine in Style",
                blockedAdsCount = 27,
                isPrivate = false
            ),
            BrowserTab(
                id = "tab-2",
                url = "https://twitter.com",
                title = "Twitter",
                blockedAdsCount = 14,
                isPrivate = false
            ),
            BrowserTab(
                id = "tab-3",
                url = "https://account.scynr.xyz/",
                title = "Scynr Portal",
                blockedAdsCount = 8,
                isPrivate = false
            )
        )
    )
    val tabs: StateFlow<List<BrowserTab>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow("tab-1")
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    val webBackTrigger = MutableStateFlow(0)
    val webForwardTrigger = MutableStateFlow(0)
    val showAdBlockSheet = MutableStateFlow(false)
    val tabSearchQuery = MutableStateFlow("")

    val activeTab: StateFlow<BrowserTab?> = MutableStateFlow<BrowserTab?>(null).apply {
        viewModelScope.launch {
            _tabs.collect { currentTabs ->
                val activeId = _activeTabId.value
                val found = currentTabs.find { it.id == activeId } ?: currentTabs.firstOrNull()
                (this@apply as MutableStateFlow).value = found
            }
        }
    }

    // Room Data
    val shortcuts: StateFlow<List<ShortcutItem>> = dao.getAllShortcuts().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val bookmarks: StateFlow<List<BookmarkItem>> = dao.getAllBookmarks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val history: StateFlow<List<HistoryItem>> = dao.getHistory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val lockedUrls: StateFlow<List<LockedUrlItem>> = dao.getLockedUrls().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // AdBlock & Power
    private val _isAdBlockEnabled = MutableStateFlow(true)
    val isAdBlockEnabled: StateFlow<Boolean> = _isAdBlockEnabled.asStateFlow()

    private val _isPowerSaverEnabled = MutableStateFlow(false)
    val isPowerSaverEnabled: StateFlow<Boolean> = _isPowerSaverEnabled.asStateFlow()

    private val _isForceDarkWebEnabled = MutableStateFlow(false)
    val isForceDarkWebEnabled: StateFlow<Boolean> = _isForceDarkWebEnabled.asStateFlow()

    private val _totalAdsBlocked = MutableStateFlow(42)
    val totalAdsBlocked: StateFlow<Int> = _totalAdsBlocked.asStateFlow()

    private val _totalTrackersBlocked = MutableStateFlow(128)
    val totalTrackersBlocked: StateFlow<Int> = _totalTrackersBlocked.asStateFlow()

    private val _dataSavedKb = MutableStateFlow(1540L)
    val dataSavedKb: StateFlow<Long> = _dataSavedKb.asStateFlow()

    // WHMCS User
    private val _whmcsUser = MutableStateFlow(
        WhmcsUser(
            email = "",
            firstName = "",
            lastName = "",
            clientId = "",
            isLoggedIn = false
        )
    )
    val whmcsUser: StateFlow<WhmcsUser> = _whmcsUser.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // Security & Locked URL Interception
    private val _pendingLockedUrl = MutableStateFlow<String?>(null)
    val pendingLockedUrl: StateFlow<String?> = _pendingLockedUrl.asStateFlow()

    private val _securityPromptReason = MutableStateFlow("Authentication Required")
    val securityPromptReason: StateFlow<String> = _securityPromptReason.asStateFlow()

    private val _isSecurityAuthenticated = MutableStateFlow(false)
    val isSecurityAuthenticated: StateFlow<Boolean> = _isSecurityAuthenticated.asStateFlow()

    // Developer / Inspect Element
    private val _consoleLogs = MutableStateFlow<List<DevConsoleMessage>>(emptyList())
    val consoleLogs: StateFlow<List<DevConsoleMessage>> = _consoleLogs.asStateFlow()

    private val _domContent = MutableStateFlow("")
    val domContent: StateFlow<String> = _domContent.asStateFlow()

    // UI Dialog & Overlay States
    val showTabSwitcher = MutableStateFlow(false)
    val showInspectElement = MutableStateFlow(false)
    val showWhmcsDialog = MutableStateFlow(false)
    val showHistoryDialog = MutableStateFlow(false)
    val showBookmarksDialog = MutableStateFlow(false)
    val showLockedUrlsVault = MutableStateFlow(false)
    val showSettingsDialog = MutableStateFlow(false)
    val showRecorderDialog = MutableStateFlow(false)
    val showAddShortcutDialog = MutableStateFlow(false)
    val showSecurityAuthPrompt = MutableStateFlow(false)

    // Downloads Engine & Flow
    val downloadEngine = DownloadEngine(dao, viewModelScope)
    val downloads: StateFlow<List<DownloadItem>> = downloadEngine.allDownloads.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Dedicated Full-Screen Navigation State
    val currentScreenState = MutableStateFlow(BrowserScreenState.BROWSER_MAIN)

    // Wallpaper & Widget Personalization
    val currentWallpaper = MutableStateFlow(WallpaperType.AURORA)
    val customWallpaperUri = MutableStateFlow<String?>(null)
    val widgetConfig = MutableStateFlow(WidgetPersonalization())

    fun navigateTo(screen: BrowserScreenState) {
        currentScreenState.value = screen
    }

    fun navigateBack(): Boolean {
        if (currentScreenState.value != BrowserScreenState.BROWSER_MAIN) {
            currentScreenState.value = BrowserScreenState.BROWSER_MAIN
            return true
        }
        return false
    }

    fun setWallpaper(type: WallpaperType) {
        currentWallpaper.value = type
    }

    fun setCustomWallpaper(uri: String?) {
        customWallpaperUri.value = uri
        currentWallpaper.value = WallpaperType.CUSTOM_PHOTO
    }

    fun updateWidgetConfig(config: WidgetPersonalization) {
        widgetConfig.value = config
    }

    fun startDownload(url: String, fileName: String = "", isTurbo: Boolean = true) {
        downloadEngine.startDownload(url, fileName, isTurbo)
    }

    fun pauseDownload(item: DownloadItem) {
        downloadEngine.pauseDownload(item)
    }

    fun resumeDownload(item: DownloadItem) {
        downloadEngine.resumeDownload(item)
    }

    fun deleteDownload(item: DownloadItem) {
        downloadEngine.deleteDownload(item)
    }

    fun clearDownloads() {
        downloadEngine.clearAll()
    }

    init {
        // Ticking timer for screen recorder
        viewModelScope.launch {
            while (true) {
                delay(1000)
                recorderManager.tickSecond()
            }
        }

        // Initialize default shortcuts if empty
        viewModelScope.launch(Dispatchers.IO) {
            val count = dao.getShortcutsCount()
            if (count == 0) {
                val defaults = listOf(
                    ShortcutItem(title = "Scynr Portal", url = "https://account.scynr.xyz/", colorHex = "#00E5FF", orderIndex = 0),
                    ShortcutItem(title = "Google", url = "https://www.google.com", colorHex = "#4285F4", orderIndex = 1),
                    ShortcutItem(title = "DuckDuckGo", url = "https://duckduckgo.com", colorHex = "#DE5833", orderIndex = 2),
                    ShortcutItem(title = "GitHub", url = "https://github.com", colorHex = "#E6EDF3", orderIndex = 3),
                    ShortcutItem(title = "Wikipedia", url = "https://www.wikipedia.org", colorHex = "#636466", orderIndex = 4),
                    ShortcutItem(title = "YouTube", url = "https://www.youtube.com", colorHex = "#FF0000", orderIndex = 5),
                    ShortcutItem(title = "Reddit", url = "https://www.reddit.com", colorHex = "#FF4500", orderIndex = 6),
                    ShortcutItem(title = "Hacker News", url = "https://news.ycombinator.com", colorHex = "#FF6600", orderIndex = 7)
                )
                dao.insertShortcuts(defaults)

                // Add sample locked URL
                dao.insertLockedUrl(
                    LockedUrlItem(
                        patternOrDomain = "facebook.com",
                        label = "Social Privacy Shield"
                    )
                )
            }
        }

        // Dismiss splash screen after 1.8s
        viewModelScope.launch {
            delay(1800)
            _isSplashVisible.value = false
        }
    }

    fun dismissSplash() {
        _isSplashVisible.value = false
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setSearchEngine(engine: String) {
        _searchEngine.value = engine
    }

    fun toggleAdBlock() {
        _isAdBlockEnabled.value = !_isAdBlockEnabled.value
    }

    fun togglePowerSaver() {
        _isPowerSaverEnabled.value = !_isPowerSaverEnabled.value
    }

    fun toggleForceDarkWeb() {
        _isForceDarkWebEnabled.value = !_isForceDarkWebEnabled.value
    }

    fun toggleTabSwitcher() {
        showTabSwitcher.value = !showTabSwitcher.value
    }

    fun toggleAdBlockSheet() {
        showAdBlockSheet.value = !showAdBlockSheet.value
    }

    fun triggerWebBack() {
        webBackTrigger.value += 1
    }

    fun triggerWebForward() {
        webForwardTrigger.value += 1
    }

    // Tab Management
    fun openNewTab(url: String = "about:home", isPrivate: Boolean = false): String {
        val newId = java.util.UUID.randomUUID().toString()
        val title = if (url == "about:home") (if (isPrivate) "Private Tab" else "Home") else "Loading..."
        val newTab = BrowserTab(
            id = newId,
            url = url,
            title = title,
            isPrivate = isPrivate
        )
        _tabs.value = _tabs.value + newTab
        _activeTabId.value = newId
        showTabSwitcher.value = false
        return newId
    }

    fun switchTab(tabId: String) {
        _activeTabId.value = tabId
        showTabSwitcher.value = false
        // Update last accessed
        _tabs.value = _tabs.value.map {
            if (it.id == tabId) it.copy(lastAccessed = System.currentTimeMillis()) else it
        }
    }

    fun closeTab(tabId: String) {
        val current = _tabs.value
        if (current.size <= 1) {
            // Keep at least one tab open
            val freshId = java.util.UUID.randomUUID().toString()
            _tabs.value = listOf(BrowserTab(id = freshId, url = "about:home", title = "Home"))
            _activeTabId.value = freshId
            return
        }

        val remaining = current.filter { it.id != tabId }
        _tabs.value = remaining

        if (_activeTabId.value == tabId) {
            _activeTabId.value = remaining.last().id
        }
    }

    fun closeAllTabs(keepPrivate: Boolean = false) {
        val remaining = if (keepPrivate) {
            _tabs.value.filter { it.isPrivate }
        } else {
            emptyList()
        }
        if (remaining.isEmpty()) {
            val freshId = java.util.UUID.randomUUID().toString()
            _tabs.value = listOf(BrowserTab(id = freshId, url = "about:home", title = "Home"))
            _activeTabId.value = freshId
        } else {
            _tabs.value = remaining
            _activeTabId.value = remaining.first().id
        }
    }

    fun updateTabUrl(
        tabId: String,
        url: String,
        title: String? = null,
        canGoBack: Boolean? = null,
        canGoForward: Boolean? = null
    ) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) {
                tab.copy(
                    url = url,
                    title = title ?: tab.title,
                    canGoBack = canGoBack ?: tab.canGoBack,
                    canGoForward = canGoForward ?: tab.canGoForward,
                    lastAccessed = System.currentTimeMillis()
                )
            } else tab
        }

        // Add to history if not private and not internal page
        val active = _tabs.value.find { it.id == tabId }
        if (active != null && !active.isPrivate && !url.startsWith("about:")) {
            viewModelScope.launch(Dispatchers.IO) {
                dao.insertHistory(
                    HistoryItem(
                        title = title ?: url,
                        url = url,
                        isPrivate = false
                    )
                )
            }
        }

        // Record step in session recorder if running
        recorderManager.recordStep(url, title ?: "")
    }

    fun updateTabProgress(tabId: String, progress: Int, isLoading: Boolean) {
        _tabs.value = _tabs.value.map {
            if (it.id == tabId) it.copy(progress = progress, isLoading = isLoading) else it
        }
    }

    fun incrementTabBlockedAds(tabId: String) {
        _totalAdsBlocked.value += 1
        _dataSavedKb.value += 45L
        _tabs.value = _tabs.value.map {
            if (it.id == tabId) it.copy(blockedAdsCount = it.blockedAdsCount + 1) else it
        }
    }

    fun toggleDesktopMode(tabId: String) {
        _tabs.value = _tabs.value.map {
            if (it.id == tabId) it.copy(isDesktopMode = !it.isDesktopMode) else it
        }
    }

    fun toggleReaderMode(tabId: String) {
        _tabs.value = _tabs.value.map {
            if (it.id == tabId) it.copy(isReaderMode = !it.isReaderMode) else it
        }
    }

    // Format query or URL
    fun resolveSearchOrUrl(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "about:home"

        if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("about:")) {
            return trimmed
        }

        // Check if looks like domain name
        val domainPattern = Regex("^[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(/.*)?$")
        if (domainPattern.matches(trimmed) && !trimmed.contains(" ")) {
            return "https://$trimmed"
        }

        // Search engine search query
        val encoded = java.net.URLEncoder.encode(trimmed, "UTF-8")
        return when (_searchEngine.value) {
            "DuckDuckGo" -> "https://duckduckgo.com/?q=$encoded"
            "Bing" -> "https://www.bing.com/search?q=$encoded"
            "Brave" -> "https://search.brave.com/search?q=$encoded"
            "Ecosia" -> "https://www.ecosia.org/search?q=$encoded"
            else -> "https://www.google.com/search?q=$encoded"
        }
    }

    // URL Lock Check
    fun checkUrlLock(targetUrl: String): Boolean {
        val lockedList = lockedUrls.value.filter { it.isLocked }.map { it.patternOrDomain }
        if (BiometricAuthManager.isUrlLocked(targetUrl, lockedList)) {
            _pendingLockedUrl.value = targetUrl
            _securityPromptReason.value = "Website Locked: ${targetUrl.substringBefore("?")}"
            showSecurityAuthPrompt.value = true
            return true // Intercepted
        }
        return false // Safe to load
    }

    fun onSecurityAuthSuccess() {
        showSecurityAuthPrompt.value = false
        _isSecurityAuthenticated.value = true
        val pending = _pendingLockedUrl.value
        if (pending != null) {
            _pendingLockedUrl.value = null
            // Load pending URL in active tab
            val tabId = _activeTabId.value
            updateTabUrl(tabId, pending, "Loading...")
        }
    }

    fun onSecurityAuthDismissed() {
        showSecurityAuthPrompt.value = false
        _pendingLockedUrl.value = null
    }

    // Shortcuts
    fun addShortcut(title: String, url: String, colorHex: String = "#00E5FF") {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertShortcut(
                ShortcutItem(
                    title = title,
                    url = resolveSearchOrUrl(url),
                    colorHex = colorHex,
                    orderIndex = shortcuts.value.size
                )
            )
        }
        showAddShortcutDialog.value = false
    }

    fun deleteShortcut(shortcut: ShortcutItem) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteShortcut(shortcut)
        }
    }

    // Bookmarks
    fun toggleBookmarkCurrentPage() {
        val tab = _tabs.value.find { it.id == _activeTabId.value } ?: return
        if (tab.url.startsWith("about:")) return

        viewModelScope.launch(Dispatchers.IO) {
            val isBookmarked = bookmarks.value.any { it.url == tab.url }
            if (isBookmarked) {
                dao.deleteBookmarkByUrl(tab.url)
            } else {
                dao.insertBookmark(
                    BookmarkItem(
                        title = tab.title.ifEmpty { tab.url },
                        url = tab.url
                    )
                )
            }
        }
    }

    fun deleteBookmark(bookmark: BookmarkItem) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteBookmark(bookmark)
        }
    }

    // History
    fun deleteHistoryItem(item: HistoryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteHistory(item)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearHistory()
        }
    }

    // Locked URLs
    fun addLockedUrl(pattern: String, label: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertLockedUrl(
                LockedUrlItem(
                    patternOrDomain = pattern.trim(),
                    label = label.ifEmpty { pattern.trim() }
                )
            )
        }
    }

    fun deleteLockedUrl(item: LockedUrlItem) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteLockedUrl(item)
        }
    }

    fun toggleLockedUrl(item: LockedUrlItem) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateLockedUrl(item.copy(isLocked = !item.isLocked))
        }
    }

    // WHMCS Login / Register / Sync
    fun loginWhmcs(email: String, pass: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Authenticating with Scynr WHMCS..."
            val result = WhmcsClient.login(email, pass)
            _isSyncing.value = false
            result.onSuccess { user ->
                _whmcsUser.value = user
                _syncMessage.value = "Welcome back, ${user.firstName}!"
                // Trigger auto sync
                syncWithWhmcs()
            }.onFailure { err ->
                _syncMessage.value = "Login error: ${err.message}"
            }
        }
    }

    fun registerWhmcs(
        firstName: String,
        lastName: String,
        email: String,
        pass: String,
        phone: String
    ) {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Creating your Scynr account..."
            val result = WhmcsClient.register(firstName, lastName, email, pass, phone)
            _isSyncing.value = false
            result.onSuccess { user ->
                _whmcsUser.value = user
                _syncMessage.value = "Registration successful!"
                syncWithWhmcs()
            }.onFailure { err ->
                _syncMessage.value = "Registration error: ${err.message}"
            }
        }
    }

    fun logoutWhmcs() {
        _whmcsUser.value = WhmcsUser(isLoggedIn = false)
        _syncMessage.value = "Logged out from Scynr."
    }

    fun syncWithWhmcs() {
        val user = _whmcsUser.value
        if (!user.isLoggedIn) {
            _syncMessage.value = "Please sign in to sync your browser data."
            return
        }

        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Packaging bookmarks, shortcuts, and settings..."

            val payload = JSONObject().apply {
                put("bookmarks_count", bookmarks.value.size)
                put("shortcuts_count", shortcuts.value.size)
                put("locked_urls_count", lockedUrls.value.size)
                put("client_id", user.clientId)
                put("timestamp", System.currentTimeMillis())
            }.toString()

            val syncRes = WhmcsClient.syncData(user, payload)
            _isSyncing.value = false
            syncRes.onSuccess { msg ->
                _whmcsUser.value = user.copy(lastSyncedTime = System.currentTimeMillis())
                _syncMessage.value = "Synced with Scynr Cloud!"
            }.onFailure {
                _syncMessage.value = "Sync failed: ${it.message}"
            }
        }
    }

    // Inspect Element / Console
    fun addConsoleLog(log: DevConsoleMessage) {
        val current = _consoleLogs.value.toMutableList()
        current.add(log)
        if (current.size > 200) current.removeAt(0)
        _consoleLogs.value = current
    }

    fun clearConsoleLogs() {
        _consoleLogs.value = emptyList()
    }

    fun setDomContent(html: String) {
        _domContent.value = html
    }
}
