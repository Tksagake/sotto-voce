package com.vivace.sottovoce.ui

import android.content.Context
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/113.0.0.0 Safari/537.36"

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val cookies = CookieManager.getInstance().getCookie(url) ?: ""

                if (cookies.contains("SAPISID")) {
                    getSharedPreferences("SottoVocePrefs", Context.MODE_PRIVATE)
                        .edit().putString("YTM_COOKIES", cookies).apply()

                    if (url?.contains("music.youtube.com") == true && !url.contains("login")) {
                        finish() // This returns the user to the MainActivity waiting in the background
                    }
                }
            }
        }
        webView.loadUrl("https://accounts.google.com/ServiceLogin?service=youtube&continue=https://music.youtube.com")
    }
}