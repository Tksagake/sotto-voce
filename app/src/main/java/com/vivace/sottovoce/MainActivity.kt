package com.vivace.sottovoce

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vivace.sottovoce.models.Track
import com.vivace.sottovoce.ui.AuthActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private var miniPlayerContainer: View? = null
    private var miniPlayerTitle: TextView? = null
    private var miniPlayerPlayBtn: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Vault Check - If empty, go to Login and PAUSE MainActivity
        val cookies = getSharedPreferences("SottoVocePrefs", Context.MODE_PRIVATE).getString("YTM_COOKIES", "")
        if (cookies.isNullOrEmpty()) {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        setContentView(R.layout.activity_main)

        // 2. Setup Player & UI
        player = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    miniPlayerPlayBtn?.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
                }
            })
        }

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        navHost?.let { findViewById<BottomNavigationView>(R.id.bottom_navigation).setupWithNavController(it.navController) }

        miniPlayerContainer = findViewById(R.id.mini_player_container)
        miniPlayerTitle = findViewById(R.id.mini_track_title)
        miniPlayerPlayBtn = findViewById(R.id.btn_mini_play)

        miniPlayerPlayBtn?.setOnClickListener {
            player?.let { if (it.isPlaying) it.pause() else it.play() }
        }
    }

    fun playTrack(track: Track) {
        lifecycleScope.launch {
            miniPlayerContainer?.visibility = View.VISIBLE
            miniPlayerTitle?.text = track.title
            val streamUrl = YtmClient(this@MainActivity).getStreamUrl(track.browseId)
            if (streamUrl != null) {
                val source = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) WebView/3.0"))
                    .createMediaSource(MediaItem.fromUri(streamUrl))
                player?.setMediaSource(source)
                player?.prepare()
                player?.play()
            } else {
                Toast.makeText(this@MainActivity, "Heist blocked by Google.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}