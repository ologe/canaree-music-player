package dev.olog.appshortcuts

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.lib.media.MusicServiceAction
import dev.olog.lib.media.MusicServiceCustomAction
import dev.olog.navigation.destination.NavigationIntents
import dev.olog.navigation.destination.musicServiceClass
import javax.inject.Inject

@AndroidEntryPoint
internal class ShortcutsActivity : AppCompatActivity() {

    @Inject
    lateinit var intents: NavigationIntents

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

        val test = when (action) {
            MusicServiceAction.PLAY.name,
            MusicServiceCustomAction.SHUFFLE.name -> intents.musicServiceClass
            else -> null
        } ?: return

        // forwards action to music service
        val serviceIntent = Intent(this, test)
        serviceIntent.action = action
        ContextCompat.startForegroundService(this, serviceIntent)
    }

}