package dev.olog.msc.presentation.utils

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

class CircularOutlineProvider : ViewOutlineProvider(){

    override fun getOutline(view: View, outline: Outline) {
        outline.setRoundRect(0,0, view.width, view.height, (view.height / 2).toFloat())
    }
}