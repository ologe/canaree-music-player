package dev.olog.presentation.prefs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorCallback
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import dev.olog.presentation.R

import dev.olog.presentation.base.ThemedActivity
import dev.olog.presentation.pro.IBilling
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.shared.theme.isImmersiveMode
import kotlinx.android.synthetic.main.activity_preferences.*
import javax.inject.Inject

class PreferencesActivity : DaggerAppCompatActivity(),
        ColorCallback,
    ThemedActivity {

    companion object {
        const val EXTRA_NEED_TO_RECREATE = "EXTRA_NEED_TO_RECREATE"
    }

    @Inject lateinit var billing: IBilling

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        themeAccentColor(this, theme)
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
        setContentView(R.layout.activity_preferences)

        if (intent?.extras?.getBoolean(EXTRA_NEED_TO_RECREATE, false) == true) {
            setResult(Activity.RESULT_OK)
        }
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun invoke(dialog: MaterialDialog, color: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val key = getString(R.string.prefs_color_accent_key)
        prefs.edit {
            putInt(key, color)
        }
        recreateActivity()
    }

    fun recreateActivity() {
        val fragment = supportFragmentManager.findFragmentByTag("prefs") as PreferencesFragment?
        fragment?.let {
            it.requestMainActivityToRecreate()
            finish()
            startActivity(Intent(this, this::class.java),
                    bundleOf(EXTRA_NEED_TO_RECREATE to true)
            )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && isImmersiveMode()){
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

}