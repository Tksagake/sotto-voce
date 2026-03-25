package com.vivace.sottovoce

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.MessageDigest

class YtmClient(private val context: Context) {
    private val client = OkHttpClient()

    private fun getVault() = context.getSharedPreferences("SottoVocePrefs", Context.MODE_PRIVATE)

    private fun generateSapisidHash(sapisid: String): String {
        val time = System.currentTimeMillis() / 1000
        val origin = "https://music.youtube.com"
        val input = "$time $sapisid $origin"
        val md = MessageDigest.getInstance("SHA-1")
        val hash = md.digest(input.toByteArray()).joinToString("") { "%02x".format(it) }
        return "SAPISIDHASH ${time}_$hash"
    }

    suspend fun fetchLibrary(): String = withContext(Dispatchers.IO) {
        val cookies = getVault().getString("YTM_COOKIES", "") ?: ""
        if (cookies.isEmpty()) return@withContext "NO_COOKIES"

        val sapisid = cookies.split("; ").find { it.startsWith("SAPISID=") }?.substringAfter("=") ?: ""
        val payload = "{\"context\":{\"client\":{\"clientName\":\"WEB_REMIX\",\"clientVersion\":\"1.20240312.01.00\"}},\"browseId\":\"FEmusic_liked_playlists\"}"

        val request = Request.Builder()
            .url("https://music.youtube.com/youtubei/v1/browse")
            .post(payload.toRequestBody("application/json".toMediaType()))
            .addHeader("Cookie", cookies)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0 Safari/537.36")
            .apply { if (sapisid.isNotEmpty()) addHeader("Authorization", generateSapisidHash(sapisid)) }
            .build()

        try { client.newCall(request).execute().body?.string() ?: "" } catch (e: Exception) { "" }
    }

    suspend fun getStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        val cookies = getVault().getString("YTM_COOKIES", "") ?: ""
        val payload = "{\"context\":{\"client\":{\"clientName\":\"TVHTML5\",\"clientVersion\":\"7.20230405.08.01\"}},\"videoId\":\"$videoId\"}"

        val request = Request.Builder()
            .url("https://www.youtube.com/youtubei/v1/player")
            .post(payload.toRequestBody("application/json".toMediaType()))
            .addHeader("Cookie", cookies)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) WebView/3.0")
            .build()

        try {
            val raw = client.newCall(request).execute().body?.string() ?: ""
            "\"url\"\\s*:\\s*\"(https://[^\"]+)\"".toRegex().find(raw)?.groupValues?.get(1)
                ?.replace("\\u0026", "&")?.replace("\\/", "/")
        } catch (e: Exception) { null }
    }
}