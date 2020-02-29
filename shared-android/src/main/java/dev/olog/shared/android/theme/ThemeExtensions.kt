package dev.olog.shared.android.theme

import android.annotation.SuppressLint
import android.content.Context
import dev.olog.shared.android.theme.ThemeUtils.THEME_SERVICE

object ThemeUtils {

    const val THEME_SERVICE = "THEME_SERVICE"

}

val Context.themeManager: ThemeManager
    @SuppressLint("WrongConstant")
    get() = applicationContext.getSystemService(THEME_SERVICE) as ThemeManager