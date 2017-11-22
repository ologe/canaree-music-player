package dev.olog.presentation.utils

import android.graphics.Color
import android.view.View
import android.view.Window

fun Window.setLightStatusBar(){
    if (isMarshmallow()){
        decorView.systemUiVisibility = (decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    }
}

fun Window.setTransparentStatusBar(){
    statusBarColor = Color.TRANSPARENT
    decorView.systemUiVisibility = (decorView.systemUiVisibility or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}