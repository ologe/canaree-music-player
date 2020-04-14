package dev.olog.feature.app.shortcuts

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import dagger.android.support.DaggerAppCompatActivity
import dev.olog.navigation.screens.Services
import javax.inject.Inject

class ShortcutsActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var services: Map<Services, @JvmSuppressWildcards Class<out Service>>

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
        val clazz = services[Services.MUSIC] ?: TODO("message")
        val serviceIntent = Intent(this, clazz)
        serviceIntent.action = action
        ContextCompat.startForegroundService(this, serviceIntent)
    }

}