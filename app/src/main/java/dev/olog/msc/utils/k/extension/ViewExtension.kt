package dev.olog.msc.utils.k.extension

import android.view.View

fun View.toggleVisibility(visible: Boolean){
    if (visible){
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}