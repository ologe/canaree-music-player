package dev.olog.ui.activity

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import dev.olog.lib.DarkDesaturatedResources
import dev.olog.platform.theme.isImmersiveMode
import dev.olog.shared.extension.isDarkMode
import dev.olog.ui.R

abstract class ThemedActivity : AppCompatActivity() {

    private var customResources: Resources? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        if (isImmersiveMode()){
            theme.applyStyle(R.style.ThemeImmersive, true)
        }
        theme.applyStyle(getAccentStyle(), true)
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && isImmersiveMode()) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    override fun getResources(): Resources {
        if (customResources == null){
            val res = super.getResources()
            val isDarkMode = res.configuration.isDarkMode()
            customResources = DarkDesaturatedResources(isDarkMode, res)
        }
        return customResources!!
    }

    private fun getAccentStyle(): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this) // TODO inject
        val color = prefs.getInt(getString(R.string.prefs_color_accent_key), ContextCompat.getColor(this, R.color.defaultColorAccent))
        return when (color){
            getColorResource(R.color.md_red_A100) -> R.style.ThemeAccentRed100
            getColorResource(R.color.md_red_A200) -> R.style.ThemeAccentRed200
            getColorResource(R.color.md_red_A400) -> R.style.ThemeAccentRed400
            getColorResource(R.color.md_red_A700) -> R.style.ThemeAccentRed700

            getColorResource(R.color.md_pink_A100) -> R.style.ThemeAccentPink100
            getColorResource(R.color.md_pink_A200) -> R.style.ThemeAccentPink200
            getColorResource(R.color.md_pink_A400) -> R.style.ThemeAccentPink400
            getColorResource(R.color.md_pink_A700) -> R.style.ThemeAccentPink700

            getColorResource(R.color.md_purple_A100) -> R.style.ThemeAccentPurple100
            getColorResource(R.color.md_purple_A200) -> R.style.ThemeAccentPurple200
            getColorResource(R.color.md_purple_A400) -> R.style.ThemeAccentPurple400
            getColorResource(R.color.md_purple_A700) -> R.style.ThemeAccentPurple700

            getColorResource(R.color.md_deep_purple_A100) -> R.style.ThemeAccentDeepPurple100
            getColorResource(R.color.md_deep_purple_A200) -> R.style.ThemeAccentDeepPurple200
            getColorResource(R.color.md_deep_purple_A400) -> R.style.ThemeAccentDeepPurple400
            getColorResource(R.color.md_deep_purple_A700) -> R.style.ThemeAccentDeepPurple700

            getColorResource(R.color.md_indigo_A100) -> R.style.ThemeAccentIndigo100
            getColorResource(R.color.md_indigo_A200) -> R.style.ThemeAccentIndigo200
            getColorResource(R.color.md_indigo_A400),
            getColorResource(R.color.md_indigo_A400_alt) -> R.style.ThemeAccentIndigo400
            getColorResource(R.color.md_indigo_A700) -> R.style.ThemeAccentIndigo700

            getColorResource(R.color.md_blue_A100) -> R.style.ThemeAccentBlue100
            getColorResource(R.color.md_blue_A200) -> R.style.ThemeAccentBlue200
            getColorResource(R.color.md_blue_A400) -> R.style.ThemeAccentBlue400
            getColorResource(R.color.md_blue_A700) -> R.style.ThemeAccentBlue700

            getColorResource(R.color.md_light_blue_A100) -> R.style.ThemeAccentLightBlue100
            getColorResource(R.color.md_light_blue_A200) -> R.style.ThemeAccentLightBlue200
            getColorResource(R.color.md_light_blue_A400) -> R.style.ThemeAccentLightBlue400
            getColorResource(R.color.md_light_blue_A700) -> R.style.ThemeAccentLightBlue700

            getColorResource(R.color.md_cyan_A100) -> R.style.ThemeAccentCyan100
            getColorResource(R.color.md_cyan_A200) -> R.style.ThemeAccentCyan200
            getColorResource(R.color.md_cyan_A400) -> R.style.ThemeAccentCyan400
            getColorResource(R.color.md_cyan_A700) -> R.style.ThemeAccentCyan700

            getColorResource(R.color.md_teal_A100) -> R.style.ThemeAccentTeal100
            getColorResource(R.color.md_teal_A200) -> R.style.ThemeAccentTeal200
            getColorResource(R.color.md_teal_A400) -> R.style.ThemeAccentTeal400
            getColorResource(R.color.md_teal_A700) -> R.style.ThemeAccentTeal700

            getColorResource(R.color.md_green_A100) -> R.style.ThemeAccentGreen100
            getColorResource(R.color.md_green_A200) -> R.style.ThemeAccentGreen200
            getColorResource(R.color.md_green_A400) -> R.style.ThemeAccentGreen400
            getColorResource(R.color.md_green_A700) -> R.style.ThemeAccentGreen700

            getColorResource(R.color.md_light_green_A100) -> R.style.ThemeAccentLightGreen100
            getColorResource(R.color.md_light_green_A200) -> R.style.ThemeAccentLightGreen200
            getColorResource(R.color.md_light_green_A400) -> R.style.ThemeAccentLightGreen400
            getColorResource(R.color.md_light_green_A700) -> R.style.ThemeAccentLightGreen700

            getColorResource(R.color.md_lime_A100) -> R.style.ThemeAccentLime100
            getColorResource(R.color.md_lime_A200) -> R.style.ThemeAccentLime200
            getColorResource(R.color.md_lime_A400) -> R.style.ThemeAccentLime400
            getColorResource(R.color.md_lime_A700) -> R.style.ThemeAccentLime700

            getColorResource(R.color.md_yellow_A100) -> R.style.ThemeAccentYellow100
            getColorResource(R.color.md_yellow_A200) -> R.style.ThemeAccentYellow200
            getColorResource(R.color.md_yellow_A400) -> R.style.ThemeAccentYellow400
            getColorResource(R.color.md_yellow_A700),
            getColorResource(R.color.md_yellow_A700_alt) -> R.style.ThemeAccentYellow700

            getColorResource(R.color.md_amber_A100) -> R.style.ThemeAccentAmber100
            getColorResource(R.color.md_amber_A200) -> R.style.ThemeAccentAmber200
            getColorResource(R.color.md_amber_A400) -> R.style.ThemeAccentAmber400
            getColorResource(R.color.md_amber_A700) -> R.style.ThemeAccentAmber700

            getColorResource(R.color.md_orange_A100) -> R.style.ThemeAccentOrange100
            getColorResource(R.color.md_orange_A200) -> R.style.ThemeAccentOrange200
            getColorResource(R.color.md_orange_A400) -> R.style.ThemeAccentOrange400
            getColorResource(R.color.md_orange_A700) -> R.style.ThemeAccentOrange700

            getColorResource(R.color.md_deep_orange_A100) -> R.style.ThemeAccentDeepOrange100
            getColorResource(R.color.md_deep_orange_A200) -> R.style.ThemeAccentDeepOrange200
            getColorResource(R.color.md_deep_orange_A400) -> R.style.ThemeAccentDeepOrange400
            getColorResource(R.color.md_deep_orange_A700) -> R.style.ThemeAccentDeepOrange700
            // prevent strange color crash
            else -> R.style.ThemeAccentIndigo400
        }
    }

    private fun getColorResource(res: Int): Int{
        return ContextCompat.getColor(this, res)
    }

}
