package dev.olog.presentation.utils

import android.view.View
import android.view.Window
import dev.olog.shared.android.theme.themeManager
import dev.olog.shared.android.dark.mode.isDarkMode
import dev.olog.core.isMarshmallow
import dev.olog.core.isOreo
import dev.olog.shared.android.extensions.colorSurface

fun Window.setLightStatusBar() {
    decorView.systemUiVisibility = 0

    var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    if (context.themeManager.isImmersive) {
        flags = flags or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    val isDarkMode = context.isDarkMode()
    if (isDarkMode) {
        navigationBarColor = context.colorSurface()
    }

    if (isMarshmallow() && !isDarkMode) {
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        if (isOreo()) {
            navigationBarColor = context.colorSurface()
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }
    decorView.systemUiVisibility = flags
}

fun Window.removeLightStatusBar() {
    decorView.systemUiVisibility = 0

    var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    if (context.themeManager.isImmersive) {
        flags = flags or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    val isDarkMode = context.isDarkMode()
    if (isDarkMode) {
        navigationBarColor = context.colorSurface()
    }

    if (isMarshmallow() && !isDarkMode) {
        if (isOreo()) {
            navigationBarColor = context.colorSurface()
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }
    decorView.systemUiVisibility = flags
}