package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.BookmarkItem
import com.example.data.model.DownloadItem
import com.example.data.model.HistoryItem
import com.example.data.model.LockedUrlItem
import com.example.data.model.SettingEntity
import com.example.data.model.ShortcutItem

@Database(
    entities = [
        ShortcutItem::class,
        BookmarkItem::class,
        HistoryItem::class,
        LockedUrlItem::class,
        SettingEntity::class,
        DownloadItem::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BrowserDatabase : RoomDatabase() {
    abstract fun browserDao(): BrowserDao

    companion object {
        @Volatile
        private var INSTANCE: BrowserDatabase? = null

        fun getDatabase(context: Context): BrowserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BrowserDatabase::class.java,
                    "arton_browser_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
