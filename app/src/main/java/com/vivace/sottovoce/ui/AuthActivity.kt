package com.vivace.sottovoce.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.vivace.sottovoce.MainActivity
import com.vivace.sottovoce.R

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // When you tap Login, go to the Main layout
        findViewById<Button>(R.id.auth_login_btn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Destroys the login page so you can't hit 'back' to it
        }
    }
}