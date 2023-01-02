package dev.olog.appshortcuts

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.media.api.FeatureMediaNavigator
import javax.inject.Inject

@AndroidEntryPoint
class ShortcutsActivity : AppCompatActivity() {

    @Inject
    lateinit var mediaNavigator: FeatureMediaNavigator

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
        // forwards action to music service
        val action = intent.action ?: return
        mediaNavigator.startService(action)
    }

}