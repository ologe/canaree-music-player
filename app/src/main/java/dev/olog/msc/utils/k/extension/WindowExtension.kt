package dev.olog.msc.utils.k.extension

import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.view.Window
import dev.olog.msc.utils.isOreo

@RequiresApi(Build.VERSION_CODES.M)
fun Window.setLightStatusBar(){
    decorView.systemUiVisibility = 0

    statusBarColor = Color.TRANSPARENT

    var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

    if (isOreo()){
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        navigationBarColor = Color.WHITE
    }
    decorView.systemUiVisibility = flags

}

@RequiresApi(Build.VERSION_CODES.M)
fun Window.removeLightStatusBar(){
    decorView.systemUiVisibility = 0

    statusBarColor = Color.TRANSPARENT

    var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    if (isOreo()){
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        navigationBarColor = Color.WHITE
    }
    decorView.systemUiVisibility = flags
}