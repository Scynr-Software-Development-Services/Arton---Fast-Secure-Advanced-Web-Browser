package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shortcuts")
data class ShortcutItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val iconUrl: String = "",
    val colorHex: String = "#00E5FF",
    val orderIndex: Int = 0,
    val isCustom: Boolean = true
)

@Entity(tableName = "bookmarks")
data class BookmarkItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val faviconUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPrivate: Boolean = false
)

@Entity(tableName = "locked_urls")
data class LockedUrlItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patternOrDomain: String,
    val label: String,
    val isLocked: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileName: String,
    val url: String,
    val filePath: String = "",
    val totalBytes: Long = 0L,
    val downloadedBytes: Long = 0L,
    val progress: Int = 100, // 0 - 100
    val status: String = "COMPLETED", // DOWNLOADING, PAUSED, COMPLETED, FAILED, WARNING
    val isSecure: Boolean = true,
    val securityWarning: String? = null,
    val isTorrent: Boolean = false,
    val torrentSeeds: Int = 0,
    val torrentPeers: Int = 0,
    val downloadSpeed: String = "14.2 MB/s",
    val mimeType: String = "",
    val isTurboAccelerated: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "browser_settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

data class BrowserTab(
    val id: String = java.util.UUID.randomUUID().toString(),
    val url: String = "about:blank",
    val title: String = "New Tab",
    val faviconUrl: String = "",
    val isPrivate: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val blockedAdsCount: Int = 0,
    val isDormant: Boolean = false,
    val isReaderMode: Boolean = false,
    val isDesktopMode: Boolean = false,
    val lastAccessed: Long = System.currentTimeMillis()
)

data class WhmcsUser(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val clientId: String = "",
    val status: String = "Active",
    val currency: String = "USD",
    val credit: String = "0.00",
    val isLoggedIn: Boolean = false,
    val lastSyncedTime: Long = 0L
)

data class DevConsoleMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val level: String = "log", // log, info, warn, error
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class RecordedFrame(
    val frameIndex: Int,
    val timestamp: Long,
    val url: String,
    val note: String = ""
)

data class BugReport(
    val timestamp: Long = System.currentTimeMillis(),
    val url: String,
    val durationSeconds: Int,
    val frameCount: Int,
    val consoleErrorsCount: Int,
    val userNotes: String,
    val systemInfo: String
)

data class WidgetPersonalization(
    val showWeather: Boolean = true,
    val showNews: Boolean = true,
    val showMatches: Boolean = true,
    val selectedCity: String = "San Francisco",
    val isCompact: Boolean = false
)

enum class WallpaperType {
    AURORA,
    NEON_MESH,
    SUNSET,
    COSMIC,
    MINIMAL_SLATE,
    CUSTOM_PHOTO
}

enum class BrowserScreenState {
    BROWSER_MAIN,
    HISTORY,
    SETTINGS,
    ACCOUNT_PROFILE,
    SCYNR_AUTH,
    URL_LOCK_VAULT,
    DOWNLOADS
}
