package dev.olog.msc.presentation.base

import android.content.Context
import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.content.res.Resources
import androidx.core.graphics.ColorUtils
import androidx.core.math.MathUtils
import dev.olog.msc.R
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.extensions.themeAttributeToResId

// TODO check on lollipop
internal class CustomResources(
    private val context: Context,
    asset: AssetManager,
    resources: Resources
) : Resources(asset, resources.displayMetrics, resources.configuration) {

//    companion object {
//        private const val DESATURATE_BY = .25f
//        private const val DESATURATE_MIN = .5f
//        private const val DESATURATE_MAX = 1f
//    }
//
//    private val colorAccentId by lazyFast { context.themeAttributeToResId(com.google.android.material.R.attr.colorAccent) }
//    private val colorPrimaryId by lazyFast { context.themeAttributeToResId(com.google.android.material.R.attr.colorPrimary) }
//
//    private val isDarkTheme by lazyFast { getBoolean(R.bool.is_dark_mode) }
//
//    override fun getColor(id: Int, theme: Theme?): Int {
//        if ((id == colorAccentId || id == colorPrimaryId) && isDarkTheme){
//            return desaturateColor(super.getColor(id, theme))
//        }
//        return super.getColor(id, theme)
//    }
//
//    override fun getColorStateList(id: Int, theme: Theme?): ColorStateList {
//        if ((id == colorAccentId || id == colorPrimaryId) && isDarkTheme){
//            return desaturateColorStateList(super.getColorStateList(id, theme))
//        }
//        return super.getColorStateList(id, theme)
//    }
//
//    private fun desaturateColorStateList(stateList: ColorStateList): ColorStateList {
//        if (stateList.isStateful){
//            stateList.changingConfigurations
//        }
//        val desaturatedColor = desaturateColor(stateList.defaultColor)
//        return ColorStateList.valueOf(desaturatedColor)
//    }
//
//    private fun desaturateColor(color: Int): Int {
//        val hsl = floatArrayOf(0f, 0f, 0f)
//        ColorUtils.colorToHSL(color, hsl)
//        hsl[1] = MathUtils.clamp(hsl[1] - DESATURATE_BY, DESATURATE_MIN, DESATURATE_MAX)
////        hsl[1] = 0f
//        return ColorUtils.HSLToColor(hsl)
//    }

}