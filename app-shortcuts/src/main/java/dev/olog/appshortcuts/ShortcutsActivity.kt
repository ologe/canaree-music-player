package dev.olog.appshortcuts

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.media.FeatureMediaNavigator
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
        featureMediaNavigator.startService(action, null)
    }

}