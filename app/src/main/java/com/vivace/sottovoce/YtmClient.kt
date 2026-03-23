package com.vivace.sottovoce

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class YtmClient(context: Context) {
    private val client = OkHttpClient()
    private val prefs = context.getSharedPreferences("SottoVocePrefs", Context.MODE_PRIVATE)
    private val cookies = prefs.getString("YTM_COOKIES", "") ?: ""

    suspend fun getStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
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

    suspend fun fetchLibrary(): String = withContext(Dispatchers.IO) {
        val payload = "{\"context\":{\"client\":{\"clientName\":\"WEB_REMIX\",\"clientVersion\":\"1.20230503.01.00\"}},\"browseId\":\"FEmusic_liked_playlists\"}"
        val request = Request.Builder()
            .url("https://music.youtube.com/youtubei/v1/browse")
            .post(payload.toRequestBody("application/json".toMediaType()))
            .addHeader("Cookie", cookies)
            .build()
        try { client.newCall(request).execute().body?.string() ?: "" } catch (e: Exception) { "" }
    }

    suspend fun fetchPlaylistDetails(playlistId: String): String = withContext(Dispatchers.IO) {
        val payload = "{\"context\":{\"client\":{\"clientName\":\"WEB_REMIX\",\"clientVersion\":\"1.20230503.01.00\"}},\"browseId\":\"$playlistId\"}"
        val request = Request.Builder()
            .url("https://music.youtube.com/youtubei/v1/browse")
            .post(payload.toRequestBody("application/json".toMediaType()))
            .addHeader("Cookie", cookies)
            .build()
        try { client.newCall(request).execute().body?.string() ?: "" } catch (e: Exception) { "" }
    }
}