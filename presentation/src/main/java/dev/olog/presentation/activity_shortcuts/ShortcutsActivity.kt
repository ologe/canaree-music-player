package dev.olog.presentation.activity_shortcuts

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import dev.olog.shared_android.Constants

class ShortcutsActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent!!)
        finish()
    }

    private fun handleIntent(intent: Intent){
        val action = intent.action
        when (action){
            Constants.SHORTCUT_PLAY -> {
                val serviceIntent = Intent()
                intent.action = Constants.SHORTCUT_PLAY
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            Constants.SHORTCUT_SHUFFLE -> {
                val serviceIntent = Intent()
                intent.action = Constants.SHORTCUT_SHUFFLE
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }
    }

}