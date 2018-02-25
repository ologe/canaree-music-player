package dev.olog.msc.presentation.utils

import android.view.View
import android.widget.PopupMenu

fun PopupMenu.addRotateAnimation(view: View?){
    view?.animate()?.rotation(90f)
    setOnDismissListener { view?.animate()?.rotation(0f) }
}