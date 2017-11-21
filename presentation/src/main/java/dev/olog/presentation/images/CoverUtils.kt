package dev.olog.presentation.images

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import dev.olog.presentation.R

object CoverUtils {

    private val RANDOM = 4

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
    )

    fun getGradient(context: Context, position: Int, source: Int = 2): Drawable {
        val drawable = ContextCompat.getDrawable(context, getDrawable(source))!!.mutate() as LayerDrawable
        val gradient = drawable.getDrawable(0) as GradientDrawable
        val pos = (position + RANDOM) % COLORS.size
        gradient.colors = COLORS[pos]
        return drawable
    }

    @DrawableRes
    private fun getDrawable(source: Int): Int {
        when (source) {
            0 -> return R.drawable.placeholder_folder
            1 -> return R.drawable.placeholder_playlist
            2 -> return R.drawable.placeholder_sound_bars
            3 -> return R.drawable.placeholder_album
            4 -> return R.drawable.placeholder_artist
            5 -> return R.drawable.placeholder_genre
        }
        return R.drawable.placeholder_bird_singing
    }

}