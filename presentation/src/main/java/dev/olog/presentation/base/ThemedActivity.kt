package dev.olog.presentation.base

import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import dev.olog.presentation.R
import dev.olog.shared.android.theme.isImmersiveMode

interface ThemedActivity {

    fun themeAccentColor(context: Context, theme: Resources.Theme){
        if (context.isImmersiveMode()){
            theme.applyStyle(dev.olog.shared.android.R.style.ThemeImmersive, true)
        }
        theme.applyStyle(getAccentStyle(context.applicationContext), true)
    }

    private fun getAccentStyle(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val color = prefs.getInt(context.getString(dev.olog.prefskeys.R.string.prefs_color_accent_key), ContextCompat.getColor(context, dev.olog.shared.android.R.color.defaultColorAccent))
        return when (color){
            getColorResource(context, dev.olog.shared.android.R.color.md_red_A100) -> dev.olog.shared.android.R.style.ThemeAccentRed100
            getColorResource(context, dev.olog.shared.android.R.color.md_red_A200) -> dev.olog.shared.android.R.style.ThemeAccentRed200
            getColorResource(context, dev.olog.shared.android.R.color.md_red_A400) -> dev.olog.shared.android.R.style.ThemeAccentRed400
            getColorResource(context, dev.olog.shared.android.R.color.md_red_A700) -> dev.olog.shared.android.R.style.ThemeAccentRed700

            getColorResource(context, dev.olog.shared.android.R.color.md_pink_A100) -> dev.olog.shared.android.R.style.ThemeAccentPink100
            getColorResource(context, dev.olog.shared.android.R.color.md_pink_A200) -> dev.olog.shared.android.R.style.ThemeAccentPink200
            getColorResource(context, dev.olog.shared.android.R.color.md_pink_A400) -> dev.olog.shared.android.R.style.ThemeAccentPink400
            getColorResource(context, dev.olog.shared.android.R.color.md_pink_A700) -> dev.olog.shared.android.R.style.ThemeAccentPink700

            getColorResource(context, dev.olog.shared.android.R.color.md_purple_A100) -> dev.olog.shared.android.R.style.ThemeAccentPurple100
            getColorResource(context, dev.olog.shared.android.R.color.md_purple_A200) -> dev.olog.shared.android.R.style.ThemeAccentPurple200
            getColorResource(context, dev.olog.shared.android.R.color.md_purple_A400) -> dev.olog.shared.android.R.style.ThemeAccentPurple400
            getColorResource(context, dev.olog.shared.android.R.color.md_purple_A700) -> dev.olog.shared.android.R.style.ThemeAccentPurple700

            getColorResource(context, dev.olog.shared.android.R.color.md_deep_purple_A100) -> dev.olog.shared.android.R.style.ThemeAccentDeepPurple100
            getColorResource(context, dev.olog.shared.android.R.color.md_deep_purple_A200) -> dev.olog.shared.android.R.style.ThemeAccentDeepPurple200
            getColorResource(context, dev.olog.shared.android.R.color.md_deep_purple_A400) -> dev.olog.shared.android.R.style.ThemeAccentDeepPurple400
            getColorResource(context, dev.olog.shared.android.R.color.md_deep_purple_A700) -> dev.olog.shared.android.R.style.ThemeAccentDeepPurple700

            getColorResource(context, dev.olog.shared.android.R.color.md_indigo_A100) -> dev.olog.shared.android.R.style.ThemeAccentIndigo100
            getColorResource(context, dev.olog.shared.android.R.color.md_indigo_A200) -> dev.olog.shared.android.R.style.ThemeAccentIndigo200
            getColorResource(context, dev.olog.shared.android.R.color.md_indigo_A400),
            getColorResource(context, dev.olog.shared.android.R.color.md_indigo_A400_alt) -> dev.olog.shared.android.R.style.ThemeAccentIndigo400
            getColorResource(context, dev.olog.shared.android.R.color.md_indigo_A700) -> dev.olog.shared.android.R.style.ThemeAccentIndigo700

            getColorResource(context, dev.olog.shared.android.R.color.md_blue_A100) -> dev.olog.shared.android.R.style.ThemeAccentBlue100
            getColorResource(context, dev.olog.shared.android.R.color.md_blue_A200) -> dev.olog.shared.android.R.style.ThemeAccentBlue200
            getColorResource(context, dev.olog.shared.android.R.color.md_blue_A400) -> dev.olog.shared.android.R.style.ThemeAccentBlue400
            getColorResource(context, dev.olog.shared.android.R.color.md_blue_A700) -> dev.olog.shared.android.R.style.ThemeAccentBlue700

            getColorResource(context, dev.olog.shared.android.R.color.md_light_blue_A100) -> dev.olog.shared.android.R.style.ThemeAccentLightBlue100
            getColorResource(context, dev.olog.shared.android.R.color.md_light_blue_A200) -> dev.olog.shared.android.R.style.ThemeAccentLightBlue200
            getColorResource(context, dev.olog.shared.android.R.color.md_light_blue_A400) -> dev.olog.shared.android.R.style.ThemeAccentLightBlue400
            getColorResource(context, dev.olog.shared.android.R.color.md_light_blue_A700) -> dev.olog.shared.android.R.style.ThemeAccentLightBlue700

            getColorResource(context, dev.olog.shared.android.R.color.md_cyan_A100) -> dev.olog.shared.android.R.style.ThemeAccentCyan100
            getColorResource(context, dev.olog.shared.android.R.color.md_cyan_A200) -> dev.olog.shared.android.R.style.ThemeAccentCyan200
            getColorResource(context, dev.olog.shared.android.R.color.md_cyan_A400) -> dev.olog.shared.android.R.style.ThemeAccentCyan400
            getColorResource(context, dev.olog.shared.android.R.color.md_cyan_A700) -> dev.olog.shared.android.R.style.ThemeAccentCyan700

            getColorResource(context, dev.olog.shared.android.R.color.md_teal_A100) -> dev.olog.shared.android.R.style.ThemeAccentTeal100
            getColorResource(context, dev.olog.shared.android.R.color.md_teal_A200) -> dev.olog.shared.android.R.style.ThemeAccentTeal200
            getColorResource(context, dev.olog.shared.android.R.color.md_teal_A400) -> dev.olog.shared.android.R.style.ThemeAccentTeal400
            getColorResource(context, dev.olog.shared.android.R.color.md_teal_A700) -> dev.olog.shared.android.R.style.ThemeAccentTeal700

            getColorResource(context, dev.olog.shared.android.R.color.md_green_A100) -> dev.olog.shared.android.R.style.ThemeAccentGreen100
            getColorResource(context, dev.olog.shared.android.R.color.md_green_A200) -> dev.olog.shared.android.R.style.ThemeAccentGreen200
            getColorResource(context, dev.olog.shared.android.R.color.md_green_A400) -> dev.olog.shared.android.R.style.ThemeAccentGreen400
            getColorResource(context, dev.olog.shared.android.R.color.md_green_A700) -> dev.olog.shared.android.R.style.ThemeAccentGreen700

            getColorResource(context, dev.olog.shared.android.R.color.md_light_green_A100) -> dev.olog.shared.android.R.style.ThemeAccentLightGreen100
            getColorResource(context, dev.olog.shared.android.R.color.md_light_green_A200) -> dev.olog.shared.android.R.style.ThemeAccentLightGreen200
            getColorResource(context, dev.olog.shared.android.R.color.md_light_green_A400) -> dev.olog.shared.android.R.style.ThemeAccentLightGreen400
            getColorResource(context, dev.olog.shared.android.R.color.md_light_green_A700) -> dev.olog.shared.android.R.style.ThemeAccentLightGreen700

            getColorResource(context, dev.olog.shared.android.R.color.md_lime_A100) -> dev.olog.shared.android.R.style.ThemeAccentLime100
            getColorResource(context, dev.olog.shared.android.R.color.md_lime_A200) -> dev.olog.shared.android.R.style.ThemeAccentLime200
            getColorResource(context, dev.olog.shared.android.R.color.md_lime_A400) -> dev.olog.shared.android.R.style.ThemeAccentLime400
            getColorResource(context, dev.olog.shared.android.R.color.md_lime_A700) -> dev.olog.shared.android.R.style.ThemeAccentLime700

            getColorResource(context, dev.olog.shared.android.R.color.md_yellow_A100) -> dev.olog.shared.android.R.style.ThemeAccentYellow100
            getColorResource(context, dev.olog.shared.android.R.color.md_yellow_A200) -> dev.olog.shared.android.R.style.ThemeAccentYellow200
            getColorResource(context, dev.olog.shared.android.R.color.md_yellow_A400) -> dev.olog.shared.android.R.style.ThemeAccentYellow400
            getColorResource(context, dev.olog.shared.android.R.color.md_yellow_A700),
            getColorResource(context, dev.olog.shared.android.R.color.md_yellow_A700_alt) -> dev.olog.shared.android.R.style.ThemeAccentYellow700

            getColorResource(context, dev.olog.shared.android.R.color.md_amber_A100) -> dev.olog.shared.android.R.style.ThemeAccentAmber100
            getColorResource(context, dev.olog.shared.android.R.color.md_amber_A200) -> dev.olog.shared.android.R.style.ThemeAccentAmber200
            getColorResource(context, dev.olog.shared.android.R.color.md_amber_A400) -> dev.olog.shared.android.R.style.ThemeAccentAmber400
            getColorResource(context, dev.olog.shared.android.R.color.md_amber_A700) -> dev.olog.shared.android.R.style.ThemeAccentAmber700

            getColorResource(context, dev.olog.shared.android.R.color.md_orange_A100) -> dev.olog.shared.android.R.style.ThemeAccentOrange100
            getColorResource(context, dev.olog.shared.android.R.color.md_orange_A200) -> dev.olog.shared.android.R.style.ThemeAccentOrange200
            getColorResource(context, dev.olog.shared.android.R.color.md_orange_A400) -> dev.olog.shared.android.R.style.ThemeAccentOrange400
            getColorResource(context, dev.olog.shared.android.R.color.md_orange_A700) -> dev.olog.shared.android.R.style.ThemeAccentOrange700

            getColorResource(context, dev.olog.shared.android.R.color.md_deep_orange_A100) -> dev.olog.shared.android.R.style.ThemeAccentDeepOrange100
            getColorResource(context, dev.olog.shared.android.R.color.md_deep_orange_A200) -> dev.olog.shared.android.R.style.ThemeAccentDeepOrange200
            getColorResource(context, dev.olog.shared.android.R.color.md_deep_orange_A400) -> dev.olog.shared.android.R.style.ThemeAccentDeepOrange400
            getColorResource(context, dev.olog.shared.android.R.color.md_deep_orange_A700) -> dev.olog.shared.android.R.style.ThemeAccentDeepOrange700
            // prevent strange color crash
            else -> dev.olog.shared.android.R.style.ThemeAccentIndigo400
        }
    }

    private fun getColorResource(context: Context, res: Int): Int{
        return ContextCompat.getColor(context, res)
    }

}