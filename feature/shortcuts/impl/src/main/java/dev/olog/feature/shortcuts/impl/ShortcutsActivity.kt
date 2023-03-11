package dev.olog.feature.shortcuts.impl

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
    lateinit var featureMediaNavigator: FeatureMediaNavigator

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
        featureMediaNavigator.startService(action)
    }

}