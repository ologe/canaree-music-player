package dev.olog.platform

interface CanChangeStatusBarColor {
    fun adjustStatusBarColor()
    fun adjustStatusBarColor(lightStatusBar: Boolean)
}