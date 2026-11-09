package com.example.security

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build

object BiometricAuthManager {

    fun isDeviceSecure(context: Context): Boolean {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        return keyguardManager?.isDeviceSecure ?: false
    }

    fun createConfirmDeviceCredentialIntent(context: Context, title: String, description: String): Intent? {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            keyguardManager?.createConfirmDeviceCredentialIntent(title, description)
        } else {
            @Suppress("DEPRECATION")
            keyguardManager?.createConfirmDeviceCredentialIntent(title, description)
        }
    }

    // Checks if a given URL matches any of the user's locked URL rules
    fun isUrlLocked(url: String, lockedPatterns: List<String>): Boolean {
        if (url.isBlank() || lockedPatterns.isEmpty()) return false
        val cleanUrl = url.lowercase().removePrefix("https://").removePrefix("http://").removePrefix("www.")
        val domain = cleanUrl.substringBefore("/").substringBefore("?")

        for (pattern in lockedPatterns) {
            val cleanPattern = pattern.lowercase().trim()
                .removePrefix("https://")
                .removePrefix("http://")
                .removePrefix("www.")
                .removePrefix("*.")
            if (cleanPattern.isEmpty()) continue

            if (domain == cleanPattern || domain.endsWith(".$cleanPattern") || cleanUrl.contains(cleanPattern)) {
                return true
            }
        }
        return false
    }
}
