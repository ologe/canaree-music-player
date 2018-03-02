package dev.olog.msc.presentation.shortcuts

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import dev.olog.msc.constants.MusicConstants
import dev.olog.msc.music.service.MusicService

class ShortcutsActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        handleIntent(intent!!)
        finish()
    }

    private fun handleIntent(intent: Intent){
        val action = intent.action
        when (action){
            MusicConstants.ACTION_PLAY -> {
                val serviceIntent = Intent(this, MusicService::class.java)
                serviceIntent.action = MusicConstants.ACTION_PLAY
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            MusicConstants.ACTION_SHUFFLE -> {
                val serviceIntent = Intent(this, MusicService::class.java)
                serviceIntent.action = MusicConstants.ACTION_SHUFFLE
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }
    }

}