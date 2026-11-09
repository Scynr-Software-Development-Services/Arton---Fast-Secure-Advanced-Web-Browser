package com.example

import com.example.data.adblock.AdBlockEngine
import com.example.security.BiometricAuthManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testAdBlockEngine() {
        // Known ad domains should be blocked
        assertTrue(AdBlockEngine.isAdUrl("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"))
        assertTrue(AdBlockEngine.isAdUrl("https://adservice.google.com/adsid/google/ui"))
        assertTrue(AdBlockEngine.isAdUrl("https://static.criteo.net/js/ld/ld.js"))
        assertTrue(AdBlockEngine.isAdUrl("https://secure.adnxs.com/seg?add=1"))

        // Standard content websites should NOT be blocked
        assertFalse(AdBlockEngine.isAdUrl("https://www.wikipedia.org/wiki/Kotlin"))
        assertFalse(AdBlockEngine.isAdUrl("https://github.com/torvalds/linux"))
        assertFalse(AdBlockEngine.isAdUrl("https://account.scynr.xyz/"))
    }

    @Test
    fun testUrlLockMatching() {
        val lockedList = listOf("facebook.com", "bank.com")

        assertTrue(BiometricAuthManager.isUrlLocked("https://www.facebook.com/login", lockedList))
        assertTrue(BiometricAuthManager.isUrlLocked("http://m.facebook.com", lockedList))
        assertTrue(BiometricAuthManager.isUrlLocked("https://online.bank.com/dashboard", lockedList))

        assertFalse(BiometricAuthManager.isUrlLocked("https://google.com", lockedList))
        assertFalse(BiometricAuthManager.isUrlLocked("https://scynr.xyz", lockedList))
    }
}
