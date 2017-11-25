package dev.olog.presentation.utils

import android.graphics.Color
import android.view.View
import android.view.Window

fun Window.setLightStatusBar(){
    decorView.systemUiVisibility = 0
    statusBarColor = Color.TRANSPARENT
    var flags = (decorView.systemUiVisibility or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    if (isMarshmallow()){
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    decorView.systemUiVisibility = flags

}

fun Window.removeLightStatusBar(){
    decorView.systemUiVisibility = 0
    statusBarColor = Color.TRANSPARENT
    decorView.systemUiVisibility = (decorView.systemUiVisibility or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}