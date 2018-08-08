package dev.olog.msc.utils.k.extension

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach


fun View.toggleVisibility(visible: Boolean, gone: Boolean){
    if (visible){
        this.visibility = View.VISIBLE
    } else {
        if (gone){
            this.visibility = View.GONE
        } else {
            this.visibility = View.INVISIBLE
        }
    }
}

fun View.setGone(){
    this.visibility = View.GONE
}

fun View.setVisible(){
    this.visibility = View.VISIBLE
}

fun View.setPaddingTop(padding: Int) {
    setPadding(paddingLeft, padding, paddingRight, paddingBottom)
}

fun View.setPaddingBottom(padding: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, padding)
}

fun View.toggleSelected(){
    this.isSelected = !this.isSelected
}

fun View.textColorPrimary(): Int {
    return context.themeAttributeToColor(android.R.attr.textColorPrimary)
}

fun View.textColorSecondary(): Int {
    return context.themeAttributeToColor(android.R.attr.textColorSecondary)
}

fun View.windowBackground(): Int {
    return context.themeAttributeToColor(android.R.attr.windowBackground)
}
// Used to tint buttons
fun View.textColorTertiary(): Int {
    return context.themeAttributeToColor(android.R.attr.textColorTertiary)
}

fun Context.colorAccent(): Int {
    return themeAttributeToColor(android.R.attr.colorAccent)
}

fun Context.textColorPrimary(): Int {
    return themeAttributeToColor(android.R.attr.textColorPrimary)
}

fun Context.textColorSecondary(): Int {
    return themeAttributeToColor(android.R.attr.textColorSecondary)
}

fun Context.windowBackground(): Int {
    return themeAttributeToColor(android.R.attr.windowBackground)
}

private fun Context.themeAttributeToColor(themeAttributeId: Int, fallbackColor: Int = Color.WHITE): Int {
    val outValue = TypedValue()
    val theme = this.theme
    val resolved = theme.resolveAttribute(themeAttributeId, outValue, true)
    if (resolved){
        return ContextCompat.getColor(this, outValue.resourceId)
    }
    return fallbackColor
}

inline fun ViewGroup.forEachRecursively(action: (view: View) -> Unit){
    forEach {
        if (it is ViewGroup){
            it.forEach(action)
        } else {
            action(it)
        }
    }
}