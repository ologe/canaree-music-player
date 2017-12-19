package dev.olog.music_service.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import dev.olog.music_service.R

object CoverUtils {

    private val COLORS = arrayOf(
            intArrayOf(0xff00c9ff.toInt(), 0xff92fe9d.toInt()),
            intArrayOf(0xfff54ea2.toInt(), 0xffff7676.toInt()),
            intArrayOf(0xff17ead9.toInt(), 0xff92fe9d.toInt()),
            intArrayOf(0xff7b4397.toInt(), 0xffdc2430.toInt()),
            intArrayOf(0xff1cd8d2.toInt(), 0xff93edc7.toInt()),
            intArrayOf(0xff1f86ef.toInt(), 0xff5641db.toInt()),
            intArrayOf(0xfff02fc2.toInt(), 0xff6094ea.toInt()),
            intArrayOf(0xff00d2ff.toInt(), 0xff3a7bd5.toInt()),
            intArrayOf(0xfff857a6.toInt(), 0xffff5858.toInt()),
            intArrayOf(0xffaaffa9.toInt(), 0xff11ffbd.toInt()),
            intArrayOf(0xff00c6ff.toInt(), 0xff0072ff.toInt()),
            intArrayOf(0xff43cea2.toInt(), 0xff185a9d.toInt()),
            intArrayOf(0xffB650DB.toInt(), 0xff2873E1.toInt()),
            intArrayOf(0xff17ead9.toInt(), 0xff6098ea.toInt())
//            intArrayOf(0xff73C8A9.toInt(), 0xff373b44.toInt()),
//            intArrayOf(0xffd38312.toInt(), 0xffa83279.toInt()),
//            intArrayOf(0xff948e99.toInt(), 0xff2e1437.toInt()),
//            intArrayOf(0xfffdfc47.toInt(), 0xff24fe41.toInt()),
//            intArrayOf(0xff83a4d4.toInt(), 0xffb6fbff.toInt()),
//            intArrayOf(0xfffe8c00.toInt(), 0xfff83600.toInt()),
//            intArrayOf(0xff00c6ff.toInt(), 0xff0072ff.toInt()),
//            intArrayOf(0xff556270.toInt(), 0xffff6b6b.toInt()),
//            intArrayOf(0xff9d50bb.toInt(), 0xff6e48aa.toInt()),
//            intArrayOf(0xffb3ffab.toInt(), 0xff12fff7.toInt()),
//            intArrayOf(0xffaaffa9.toInt(), 0xff11ffbd.toInt()),
//            intArrayOf(0xffff4e50.toInt(), 0xfff9d423.toInt()),
//            intArrayOf(0xfff857a6.toInt(), 0xffff5858.toInt()),
//            intArrayOf(0xff4b6cb7.toInt(), 0xff122848.toInt()),
//            intArrayOf(0xfffc354c.toInt(), 0xff0abfbc.toInt()),
//            intArrayOf(0xffe43a15.toInt(), 0xffe65245.toInt()),
//            intArrayOf(0xfff52c82.toInt(), 0xff49a09d.toInt()),
//            intArrayOf(0xffed4264.toInt(), 0xffffedbc.toInt()),
//            intArrayOf(0xffdc2424.toInt(), 0xff4a569d.toInt()),
//            intArrayOf(0xff24c6dc.toInt(), 0xff514a9d.toInt()),
//            intArrayOf(0xff1cd8d2.toInt(), 0xff93edc7.toInt()),
//            intArrayOf(0xff5c258d.toInt(), 0xff4389a2.toInt()),
//            intArrayOf(0xff4776e6.toInt(), 0xff8e54e9.toInt()),
//            intArrayOf(0xff1fa2ff.toInt(), 0xffa6ffcb.toInt()),
//            intArrayOf(0xffff512f.toInt(), 0xffdd2476.toInt()),
//            intArrayOf(0xff02aab0.toInt(), 0xff00cdac.toInt()),
//            intArrayOf(0xffd31027.toInt(), 0xffea384d.toInt()),
//            intArrayOf(0xffec008c.toInt(), 0xfffc6767.toInt()),
//            intArrayOf(0xff1488cc.toInt(), 0xff2b32b2.toInt()),
//            intArrayOf(0xffffe259.toInt(), 0xffffa751.toInt()),
//            intArrayOf(0xffbc4e9c.toInt(), 0xfff80759.toInt()),
//            intArrayOf(0xff11998e.toInt(), 0xff38ef7d.toInt()),
//            intArrayOf(0xfffc466b.toInt(), 0xff3f5efb.toInt()),
//            intArrayOf(0xff00f260.toInt(), 0xff0575e6.toInt()),
//            intArrayOf(0xff7f00ff.toInt(), 0xffe100ff.toInt()),
//            intArrayOf(0xff396afc.toInt(), 0xff2948ff.toInt()),
//            intArrayOf(0xff0cebeb.toInt(), 0xff29ffc6.toInt())
    )

    fun getGradient(context: Context, id: Long): Drawable {
        val drawable = ContextCompat.getDrawable(context, getDrawable()) as LayerDrawable
        val gradient = drawable.getDrawable(0) as GradientDrawable
        val pos = id.toInt() % COLORS.size
        gradient.colors = COLORS[Math.abs(pos)]
        return drawable
    }

    @DrawableRes
    private fun getDrawable(): Int = R.drawable.placeholder_musical_note

}