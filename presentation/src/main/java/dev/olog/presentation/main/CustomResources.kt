package dev.olog.presentation.main

import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.core.graphics.ColorUtils
import dev.olog.shared.utils.clamp

class CustomResources(
    private val isDarkMode: Boolean,
    asset: AssetManager,
    metrics: DisplayMetrics,
    configuration: Configuration
) : Resources(asset, metrics, configuration) {

    private val cache = mutableMapOf<Int, ColorStateList>()

    override fun getColor(id: Int, theme: Theme?): Int {
        if (isDarkMode){
            return desaturate(super.getColor(id, theme))
        }
        return super.getColor(id, theme)
    }

    override fun getColorStateList(id: Int, theme: Theme?): ColorStateList {
        if (!isDarkMode){
            return super.getColorStateList(id, theme)
        }

        val resolved = super.getColorStateList(id, theme)

        val cached = cache[id]
        if (cached != null) {
            return cached
        }

        try {
            val stateList = ColorStateList(
                resolved.getStates(),
                desaturateGroup(resolved.getColors())
            )
            cache[id] = stateList
            return stateList
        } catch (ex: Exception) {
            ex.printStackTrace()
            return resolved
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun ColorStateList.getStates(): Array<IntArray> {
        val method = ColorStateList::class.java.getMethod("getStates")
        method.isAccessible = true
        return method.invoke(this) as Array<IntArray>
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun ColorStateList.getColors(): IntArray {
        val method = ColorStateList::class.java.getMethod("getColors")
        method.isAccessible = true
        return method.invoke(this) as IntArray
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun desaturateGroup(colors: IntArray): IntArray{
        for (index in 0 until colors.size){
            colors[index] = desaturate(colors[index])
        }
        return colors
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun desaturate(color: Int): Int {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(color, hsl)
        if (hsl[1] > .5f){
            hsl[1] = clamp(hsl[1] - .3f, .5f, 1f)
        }
        return ColorUtils.HSLToColor(hsl)
    }

}