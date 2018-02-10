package dev.olog.msc.presentation.shortcuts

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import dagger.android.support.DaggerAppCompatActivity
import dev.olog.shared_android.Constants
import dev.olog.shared_android.interfaces.MusicServiceClass
import javax.inject.Inject

class ShortcutsActivity : DaggerAppCompatActivity(){

    @Inject lateinit var serviceClass: MusicServiceClass

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
            Constants.SHORTCUT_PLAY -> {
                val serviceIntent = Intent(this, serviceClass.get())
                serviceIntent.action = Constants.SHORTCUT_PLAY
                ContextCompat.startForegroundService(this, serviceIntent)
            }
            Constants.SHORTCUT_SHUFFLE -> {
                val serviceIntent = Intent(this, serviceClass.get())
                serviceIntent.action = Constants.SHORTCUT_SHUFFLE
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }
    }

}