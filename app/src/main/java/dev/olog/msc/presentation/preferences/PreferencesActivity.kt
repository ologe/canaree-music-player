package dev.olog.msc.presentation.preferences

import android.app.Activity
import android.os.Bundle
import android.support.annotation.StyleRes
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.pro.IBilling
import dev.olog.msc.utils.k.extension.setLightStatusBar
import kotlinx.android.synthetic.main.activity_preferences.*
import javax.inject.Inject

class PreferencesActivity : DaggerAppCompatActivity() {

    companion object {
        const val REQUEST_CODE = 1221
        const val EXTRA_NEED_TO_RECREATE = "EXTRA_NEED_TO_RECREATE"
    }

    @Inject lateinit var billing: IBilling

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setTheme(getActivityTheme())
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


}