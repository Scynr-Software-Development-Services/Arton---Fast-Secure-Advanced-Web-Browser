package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BookmarkItem
import com.example.data.model.HistoryItem
import com.example.data.model.LockedUrlItem
import com.example.data.model.SettingEntity
import com.example.data.model.ShortcutItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BrowserDao {

    // Shortcuts
    @Query("SELECT * FROM shortcuts ORDER BY orderIndex ASC")
    fun getAllShortcuts(): Flow<List<ShortcutItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortcut(shortcut: ShortcutItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortcuts(shortcuts: List<ShortcutItem>)

    @Update
    suspend fun updateShortcut(shortcut: ShortcutItem)

    @Delete
    suspend fun deleteShortcut(shortcut: ShortcutItem)

    @Query("SELECT COUNT(*) FROM shortcuts")
    suspend fun getShortcutsCount(): Int

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkItem): Long

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkItem)

    @Query("DELETE FROM bookmarks WHERE url = :url")
    suspend fun deleteBookmarkByUrl(url: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE url = :url LIMIT 1)")
    fun isBookmarked(url: String): Flow<Boolean>

    // History
    @Query("SELECT * FROM history WHERE isPrivate = 0 ORDER BY timestamp DESC LIMIT 200")
    fun getHistory(): Flow<List<HistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: HistoryItem): Long

    @Delete
    suspend fun deleteHistory(item: HistoryItem)

    @Query("DELETE FROM history")
    suspend fun clearHistory()

    // Locked URLs
    @Query("SELECT * FROM locked_urls ORDER BY createdAt DESC")
    fun getLockedUrls(): Flow<List<LockedUrlItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLockedUrl(item: LockedUrlItem): Long

    @Update
    suspend fun updateLockedUrl(item: LockedUrlItem)

    @Delete
    suspend fun deleteLockedUrl(item: LockedUrlItem)

    // Downloads
    @Query("SELECT * FROM downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<com.example.data.model.DownloadItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(item: com.example.data.model.DownloadItem): Long

    @Update
    suspend fun updateDownload(item: com.example.data.model.DownloadItem)

    @Delete
    suspend fun deleteDownload(item: com.example.data.model.DownloadItem)

    @Query("DELETE FROM downloads")
    suspend fun clearDownloads()

    // Settings
    @Query("SELECT value FROM browser_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: SettingEntity)
}
