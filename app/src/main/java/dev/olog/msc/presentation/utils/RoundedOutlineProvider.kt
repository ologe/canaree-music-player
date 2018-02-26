package dev.olog.msc.presentation.utils

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import dev.olog.msc.utils.k.extension.dip

class RoundedOutlineProvider : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        val corner = view.context.dip(5).toFloat()
        outline.setRoundRect(0 , 0, view.width, view.height, corner)
    }
}