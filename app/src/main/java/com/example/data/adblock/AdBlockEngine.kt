package com.example.data.adblock

import android.net.Uri
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object AdBlockEngine {

    val totalBlockedCount = AtomicInteger(0)
    private val tabBlockedCounters = ConcurrentHashMap<String, AtomicInteger>()

    // Core ad & tracking networks
    private val blockedDomains: Set<String> = hashSetOf(
        "doubleclick.net",
        "googleadservices.com",
        "googlesyndication.com",
        "adservice.google.com",
        "pagead2.googlesyndication.com",
        "adservice.google.com",
        "taboola.com",
        "outbrain.com",
        "adroll.com",
        "criteo.com",
        "criteo.net",
        "popads.net",
        "adnxs.com",
        "rubiconproject.com",
        "pubmatic.com",
        "casalemedia.com",
        "openx.net",
        "bidswitch.net",
        "amazon-adsystem.com",
        "advertising.com",
        "moatads.com",
        "scorecardresearch.com",
        "quantserve.com",
        "applovin.com",
        "adcolony.com",
        "vungle.com",
        "unityads.unity3d.com",
        "chartbeat.com",
        "hotjar.com",
        "mouseflow.com",
        "admob.com",
        "inmobi.com",
        "flurry.com",
        "zedo.com",
        "revcontent.com",
        "mgid.com",
        "adblade.com",
        "adsterra.com",
        "exoclick.com",
        "propellerads.com",
        "popcash.net",
        "yllix.com",
        "clickadu.com",
        "adreactor.com",
        "trafficjunky.com",
        "smartadserver.com",
        "yieldmo.com",
        "sharethrough.com",
        "teads.tv",
        "stickyadstv.com",
        "indexexchange.com",
        "sovrn.com",
        "media.net",
        "advertising.amazon.com",
        "analytics.twitter.com",
        "ad.mail.ru",
        "yandex.ru/ads",
        "an.yandex.ru"
    )

    private val adKeywords = listOf(
        "/ads/", "/ad/", "/adserver/", "/banner/", "ad_banner",
        "doubleclick", "googlesyndication", "googleadservices",
        "sponsored", "popunder", "popupad", "interstitial_ad"
    )

    fun resetTabCounter(tabId: String) {
        tabBlockedCounters[tabId]?.set(0)
    }

    fun getTabBlockedCount(tabId: String): Int {
        return tabBlockedCounters[tabId]?.get() ?: 0
    }

    fun extractHost(url: String): String {
        var clean = url.trim().lowercase()
        val schemeIndex = clean.indexOf("://")
        if (schemeIndex != -1) {
            clean = clean.substring(schemeIndex + 3)
        }
        val slashIndex = clean.indexOf('/')
        if (slashIndex != -1) {
            clean = clean.substring(0, slashIndex)
        }
        val portIndex = clean.indexOf(':')
        if (portIndex != -1) {
            clean = clean.substring(0, portIndex)
        }
        return clean
    }

    fun isAdUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val host = extractHost(url)
        if (host.isEmpty()) return false

        // Check if host matches blocked domains or ends with any blocked domain
        for (domain in blockedDomains) {
            if (host == domain || host.endsWith(".$domain")) {
                return true
            }
        }

        // Check path keywords for ad markers
        val lowerUrl = url.lowercase()
        for (kw in adKeywords) {
            if (lowerUrl.contains(kw)) {
                return true
            }
        }

        return false
    }

    fun recordBlock(tabId: String) {
        totalBlockedCount.incrementAndGet()
        tabBlockedCounters.computeIfAbsent(tabId) { AtomicInteger(0) }.incrementAndGet()
    }

    fun createEmptyResponse(): WebResourceResponse {
        return WebResourceResponse(
            "text/plain",
            "UTF-8",
            ByteArrayInputStream(ByteArray(0))
        )
    }

    // Cosmetic filtering CSS rules injected into web pages to hide residual ad slots
    const val COSMETIC_FILTER_JS: String = """
        (function() {
            var css = `
                .ad-container, .adsbygoogle, [id^="google_ads_"], [id*="-ad-"],
                .ad-box, .ad-banner, .advertisement, .ad-wrapper,
                .ad_unit, .dfp-ad, [class*="sponsored-post"], [class*="ad-slot"],
                .native-ad, .ad-placeholder, #ad-leaderboard, .top-ad,
                .bottom-ad, iframe[src*="doubleclick"], iframe[src*="adservice"] {
                    display: none !important;
                    height: 0 !important;
                    visibility: hidden !important;
                    pointer-events: none !important;
                }
            `;
            var style = document.createElement('style');
            style.type = 'text/css';
            style.id = 'arton-adblock-cosmetic';
            if (style.styleSheet) {
                style.styleSheet.cssText = css;
            } else {
                style.appendChild(document.createTextNode(css));
            }
            if (!document.getElementById('arton-adblock-cosmetic')) {
                (document.head || document.documentElement).appendChild(style);
            }
        })();
    """

    // Reader Mode JS: extracts article title and readable body content
    const val READER_MODE_EXTRACTOR_JS: String = """
        (function() {
            var article = document.querySelector('article') || 
                          document.querySelector('.article-content') || 
                          document.querySelector('.post-content') || 
                          document.querySelector('main') || 
                          document.body;
            var title = document.querySelector('h1')?.innerText || document.title;
            return JSON.stringify({
                title: title,
                content: article ? article.innerHTML : document.body.innerHTML
            });
        })();
    """

    // Dark web mode injection (force dark rendering of white web pages)
    const val FORCE_DARK_MODE_JS: String = """
        (function() {
            if (!document.getElementById('arton-force-dark')) {
                var style = document.createElement('style');
                style.id = 'arton-force-dark';
                style.innerHTML = `
                    html { filter: invert(90%) hue-rotate(180deg) !important; background: #121212 !important; }
                    img, video, iframe, canvas, svg, [style*="background-image"] {
                        filter: invert(100%) hue-rotate(180deg) !important;
                    }
                `;
                (document.head || document.documentElement).appendChild(style);
            }
        })();
    """

    const val REMOVE_FORCE_DARK_MODE_JS: String = """
        (function() {
            var el = document.getElementById('arton-force-dark');
            if (el) el.remove();
        })();
    """
}
