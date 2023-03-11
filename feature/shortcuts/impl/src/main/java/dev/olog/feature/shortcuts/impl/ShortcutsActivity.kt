package dev.olog.feature.shortcuts.impl

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.intents.Classes

@AndroidEntryPoint
class ShortcutsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        handleIntent(intent!!)
        finish()
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action ?: return

        // forwards action to music service
        val serviceIntent = Intent(this, Class.forName(Classes.SERVICE_MUSIC))
        serviceIntent.action = action
        ContextCompat.startForegroundService(this, serviceIntent)
    }

}