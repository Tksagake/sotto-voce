package com.vivace.sottovoce

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val miniPlayer = findViewById<View>(R.id.mini_player_include)

        bottomNav.setupWithNavController(navController)

        // WHEN THEY TAP THE MINI PLAYER -> OPEN NOW PLAYING SCREEN
        miniPlayer.setOnClickListener {
            navController.navigate(R.id.nav_now_playing)
        }

        // LISTEN FOR PAGE CHANGES TO HIDE THE BOTTOM BARS
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_now_playing || destination.id == R.id.nav_profile) {
                // Hide bars on Now Playing and Profile screens
                bottomNav.visibility = View.GONE
                miniPlayer.visibility = View.GONE
            } else {
                // Show everywhere else
                bottomNav.visibility = View.VISIBLE
                miniPlayer.visibility = View.VISIBLE
            }
        }
    }
}