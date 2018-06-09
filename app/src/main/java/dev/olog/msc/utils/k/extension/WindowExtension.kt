package dev.olog.msc.utils.k.extension

import android.graphics.Color
import android.view.View
import android.view.Window
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.isMarshmallow
import dev.olog.msc.utils.isOreo

fun Window.setLightStatusBar(){
    decorView.systemUiVisibility = 0

    statusBarColor = Color.TRANSPARENT

    var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    if (isMarshmallow() && !AppTheme.isDarkTheme()){
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        if (isOreo()){
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            navigationBarColor = Color.TRANSPARENT
        }
    }
    decorView.systemUiVisibility = flags
}

fun Window.removeLightStatusBar(){

    decorView.systemUiVisibility = 0

    statusBarColor = Color.TRANSPARENT

    var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    if (isOreo() && !AppTheme.isDarkTheme()){
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        navigationBarColor = Color.TRANSPARENT
    }
    decorView.systemUiVisibility = flags
}