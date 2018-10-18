package dev.olog.msc.presentation.preferences

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.annotation.StyleRes
import androidx.core.content.edit
import androidx.core.os.bundleOf
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorCallback
import dagger.android.AndroidInjection
import dev.olog.msc.R
import dev.olog.msc.dagger.base.DaggerActivity
import dev.olog.msc.presentation.base.ThemedActivity
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.pro.IBilling
import dev.olog.msc.utils.k.extension.setLightStatusBar
import kotlinx.android.synthetic.main.activity_preferences.*
import javax.inject.Inject

class PreferencesActivity : DaggerActivity(),
        ColorCallback,
        ThemedActivity {

    companion object {
        const val REQUEST_CODE = 1221
        const val EXTRA_NEED_TO_RECREATE = "EXTRA_NEED_TO_RECREATE"
    }

    @Inject lateinit var billing: IBilling

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setTheme(getActivityTheme())
        themeAccentColor(theme)
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

    @StyleRes
    private fun getActivityTheme() = when {
        AppTheme.isWhiteMode() -> R.style.AppThemeWhite
        AppTheme.isGrayMode() -> R.style.AppThemeGray
        AppTheme.isDarkMode() -> R.style.AppThemeDark
        AppTheme.isBlackMode() -> R.style.AppThemeBlack
        else -> throw IllegalStateException("invalid theme")
    }

    override fun invoke(dialog: MaterialDialog, color: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val key = getString(if (AppTheme.isWhiteTheme()) R.string.prefs_accent_light_key else R.string.prefs_accent_dark_key)
        prefs.edit {
            putInt(key, color)
        }
        val fragment = supportFragmentManager.findFragmentByTag("prefs") as PreferencesFragment?
        fragment?.let {
            it.requestMainActivityToRecreate()
            finish()
            startActivity(Intent(this, this::class.java),
                    bundleOf(EXTRA_NEED_TO_RECREATE to true)
            )
        }
    }
}