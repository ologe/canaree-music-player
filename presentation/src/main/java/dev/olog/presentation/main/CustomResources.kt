package dev.olog.presentation.main

import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import dev.olog.shared.android.utils.ColorUtils

// TODO check after android Q documentation is available, docs says to use Context.createConfigurationContext
@Suppress("DEPRECATION")
class CustomResources(
    private val isDarkMode: Boolean,
    asset: AssetManager,
    metrics: DisplayMetrics,
    configuration: Configuration
) : Resources(asset, metrics, configuration) {

    private val cache = mutableMapOf<Int, ColorStateList>()

    override fun getColor(id: Int, theme: Theme?): Int {
        if (isDarkMode){
            return ColorUtils.desaturate(super.getColor(id, theme))
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
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return resolved
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun ColorStateList.getStates(): Array<IntArray> {
        val method = ColorStateList::class.java.getMethod("getStates")
        method.isAccessible = true
        return method.invoke(this) as Array<IntArray>
    }

    private fun ColorStateList.getColors(): IntArray {
        val method = ColorStateList::class.java.getMethod("getColors")
        method.isAccessible = true
        return method.invoke(this) as IntArray
    }

    private fun desaturateGroup(colors: IntArray): IntArray{
        for (index in 0 until colors.size){
            colors[index] = ColorUtils.desaturate(colors[index])
        }
        return colors
    }

}