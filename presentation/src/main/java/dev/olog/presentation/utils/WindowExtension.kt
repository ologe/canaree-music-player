package dev.olog.presentation.utils

import android.view.View
import android.view.Window
import dev.olog.platform.BuildVersion
import dev.olog.platform.extension.colorSurface
import dev.olog.platform.extension.isDarkMode
import dev.olog.platform.theme.isImmersiveMode

fun Window.setLightStatusBar() {
    decorView.systemUiVisibility = 0

    var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    if (context.isImmersiveMode()) {
        flags = flags or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    val isDarkMode = context.isDarkMode()
    if (isDarkMode) {
        navigationBarColor = context.colorSurface()
    }

    if (BuildVersion.isMarshmallow() && !isDarkMode) {
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        if (BuildVersion.isOreo()) {
            navigationBarColor = context.colorSurface()
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }
    decorView.systemUiVisibility = flags
}

fun Window.removeLightStatusBar() {
    decorView.systemUiVisibility = 0

    var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    if (context.isImmersiveMode()) {
        flags = flags or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    val isDarkMode = context.isDarkMode()
    if (isDarkMode) {
        navigationBarColor = context.colorSurface()
    }

    if (BuildVersion.isMarshmallow() && !isDarkMode) {
        if (BuildVersion.isOreo()) {
            navigationBarColor = context.colorSurface()
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }
    decorView.systemUiVisibility = flags
}