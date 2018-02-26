package dev.olog.msc.utils.k.extension

import android.view.View


fun View.toggleVisibility(visible: Boolean){
    if (visible){
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun View.setGone(){
    this.visibility = View.GONE
}

fun View.setVisible(){
    this.visibility = View.VISIBLE
}

fun View.isVisible() = this.visibility == View.VISIBLE