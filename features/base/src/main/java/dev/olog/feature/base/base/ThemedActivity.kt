package dev.olog.feature.base.base

import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import dev.olog.feature.base.R
import dev.olog.shared.android.theme.immersiveAmbient

interface ThemedActivity {

    fun themeAccentColor(context: Context, theme: Resources.Theme){
        if (context.immersiveAmbient.isEnabled){
            theme.applyStyle(R.style.ThemeImmersive, true)
        }
        theme.applyStyle(getAccentStyle(context.applicationContext), true)
    }

    private fun getAccentStyle(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val color = prefs.getInt(context.getString(R.string.prefs_color_accent_key), ContextCompat.getColor(context, R.color.defaultColorAccent))
        return when (color){
            getColorResource(context, R.color.md_red_A100) -> R.style.ThemeAccentRed100
            getColorResource(context, R.color.md_red_A200) -> R.style.ThemeAccentRed200
            getColorResource(context, R.color.md_red_A400) -> R.style.ThemeAccentRed400
            getColorResource(context, R.color.md_red_A700) -> R.style.ThemeAccentRed700

            getColorResource(context, R.color.md_pink_A100) -> R.style.ThemeAccentPink100
            getColorResource(context, R.color.md_pink_A200) -> R.style.ThemeAccentPink200
            getColorResource(context, R.color.md_pink_A400) -> R.style.ThemeAccentPink400
            getColorResource(context, R.color.md_pink_A700) -> R.style.ThemeAccentPink700

            getColorResource(context, R.color.md_purple_A100) -> R.style.ThemeAccentPurple100
            getColorResource(context, R.color.md_purple_A200) -> R.style.ThemeAccentPurple200
            getColorResource(context, R.color.md_purple_A400) -> R.style.ThemeAccentPurple400
            getColorResource(context, R.color.md_purple_A700) -> R.style.ThemeAccentPurple700

            getColorResource(context, R.color.md_deep_purple_A100) -> R.style.ThemeAccentDeepPurple100
            getColorResource(context, R.color.md_deep_purple_A200) -> R.style.ThemeAccentDeepPurple200
            getColorResource(context, R.color.md_deep_purple_A400) -> R.style.ThemeAccentDeepPurple400
            getColorResource(context, R.color.md_deep_purple_A700) -> R.style.ThemeAccentDeepPurple700

            getColorResource(context, R.color.md_indigo_A100) -> R.style.ThemeAccentIndigo100
            getColorResource(context, R.color.md_indigo_A200) -> R.style.ThemeAccentIndigo200
            getColorResource(context, R.color.md_indigo_A400),
            getColorResource(context, R.color.md_indigo_A400_alt) -> R.style.ThemeAccentIndigo400
            getColorResource(context, R.color.md_indigo_A700) -> R.style.ThemeAccentIndigo700

            getColorResource(context, R.color.md_blue_A100) -> R.style.ThemeAccentBlue100
            getColorResource(context, R.color.md_blue_A200) -> R.style.ThemeAccentBlue200
            getColorResource(context, R.color.md_blue_A400) -> R.style.ThemeAccentBlue400
            getColorResource(context, R.color.md_blue_A700) -> R.style.ThemeAccentBlue700

            getColorResource(context, R.color.md_light_blue_A100) -> R.style.ThemeAccentLightBlue100
            getColorResource(context, R.color.md_light_blue_A200) -> R.style.ThemeAccentLightBlue200
            getColorResource(context, R.color.md_light_blue_A400) -> R.style.ThemeAccentLightBlue400
            getColorResource(context, R.color.md_light_blue_A700) -> R.style.ThemeAccentLightBlue700

            getColorResource(context, R.color.md_cyan_A100) -> R.style.ThemeAccentCyan100
            getColorResource(context, R.color.md_cyan_A200) -> R.style.ThemeAccentCyan200
            getColorResource(context, R.color.md_cyan_A400) -> R.style.ThemeAccentCyan400
            getColorResource(context, R.color.md_cyan_A700) -> R.style.ThemeAccentCyan700

            getColorResource(context, R.color.md_teal_A100) -> R.style.ThemeAccentTeal100
            getColorResource(context, R.color.md_teal_A200) -> R.style.ThemeAccentTeal200
            getColorResource(context, R.color.md_teal_A400) -> R.style.ThemeAccentTeal400
            getColorResource(context, R.color.md_teal_A700) -> R.style.ThemeAccentTeal700

            getColorResource(context, R.color.md_green_A100) -> R.style.ThemeAccentGreen100
            getColorResource(context, R.color.md_green_A200) -> R.style.ThemeAccentGreen200
            getColorResource(context, R.color.md_green_A400) -> R.style.ThemeAccentGreen400
            getColorResource(context, R.color.md_green_A700) -> R.style.ThemeAccentGreen700

            getColorResource(context, R.color.md_light_green_A100) -> R.style.ThemeAccentLightGreen100
            getColorResource(context, R.color.md_light_green_A200) -> R.style.ThemeAccentLightGreen200
            getColorResource(context, R.color.md_light_green_A400) -> R.style.ThemeAccentLightGreen400
            getColorResource(context, R.color.md_light_green_A700) -> R.style.ThemeAccentLightGreen700

            getColorResource(context, R.color.md_lime_A100) -> R.style.ThemeAccentLime100
            getColorResource(context, R.color.md_lime_A200) -> R.style.ThemeAccentLime200
            getColorResource(context, R.color.md_lime_A400) -> R.style.ThemeAccentLime400
            getColorResource(context, R.color.md_lime_A700) -> R.style.ThemeAccentLime700

            getColorResource(context, R.color.md_yellow_A100) -> R.style.ThemeAccentYellow100
            getColorResource(context, R.color.md_yellow_A200) -> R.style.ThemeAccentYellow200
            getColorResource(context, R.color.md_yellow_A400) -> R.style.ThemeAccentYellow400
            getColorResource(context, R.color.md_yellow_A700),
            getColorResource(context, R.color.md_yellow_A700_alt) -> R.style.ThemeAccentYellow700

            getColorResource(context, R.color.md_amber_A100) -> R.style.ThemeAccentAmber100
            getColorResource(context, R.color.md_amber_A200) -> R.style.ThemeAccentAmber200
            getColorResource(context, R.color.md_amber_A400) -> R.style.ThemeAccentAmber400
            getColorResource(context, R.color.md_amber_A700) -> R.style.ThemeAccentAmber700

            getColorResource(context, R.color.md_orange_A100) -> R.style.ThemeAccentOrange100
            getColorResource(context, R.color.md_orange_A200) -> R.style.ThemeAccentOrange200
            getColorResource(context, R.color.md_orange_A400) -> R.style.ThemeAccentOrange400
            getColorResource(context, R.color.md_orange_A700) -> R.style.ThemeAccentOrange700

            getColorResource(context, R.color.md_deep_orange_A100) -> R.style.ThemeAccentDeepOrange100
            getColorResource(context, R.color.md_deep_orange_A200) -> R.style.ThemeAccentDeepOrange200
            getColorResource(context, R.color.md_deep_orange_A400) -> R.style.ThemeAccentDeepOrange400
            getColorResource(context, R.color.md_deep_orange_A700) -> R.style.ThemeAccentDeepOrange700
            // prevent strange color crash
            else -> R.style.ThemeAccentIndigo400
        }
    }

    private fun getColorResource(context: Context, res: Int): Int{
        return ContextCompat.getColor(context, res)
    }

}