package dev.olog.shared_android

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.preference.PreferenceManager
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import java.util.*

object CoverUtils {

    var isIconDark = false

    private val COLORS = mutableListOf (
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
            intArrayOf(0xff17ead9.toInt(), 0xff6098ea.toInt()),
//            intArrayOf(0xFF402873.toInt(), 0xFF1c0e3b.toInt()), too dark
            intArrayOf(0xFF38ee7e.toInt(), 0xFF139c8e.toInt()),
            intArrayOf(0xFF38cedc.toInt(), 0xFF5a89e5.toInt()),
            intArrayOf(0xFF1585cb.toInt(), 0xFF2a36b3.toInt()),
            intArrayOf(0xFF994fbb.toInt(), 0xFF3034b3.toInt()),
            intArrayOf(0xFF8300ff.toInt(), 0xFFdd00ff.toInt()),
            intArrayOf(0xFFdf2674.toInt(), 0xFFfe4f32.toInt()),
            intArrayOf(0xFF840481.toInt(), 0xFFe26092.toInt()),
            intArrayOf(0xFFff6062.toInt(), 0xFFff9666.toInt()),
            intArrayOf(0xFFfc4e1b.toInt(), 0xFFf8b333.toInt()),
            intArrayOf(0xFFf79f32.toInt(), 0xFFfcca1c.toInt())
    )

    fun initialize(context: Context){
        // on every launch apps covers will be different
        isIconDark = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.prefs_icon_color_key), true)

        Collections.shuffle(COLORS)
    }

    fun getGradient(context: Context, position: Int, source: Int = 2): Drawable {
        return get(context, position, getDrawable(source))
    }

    fun getGradientForNotification(context: Context, id: Long): Drawable {
        return get(context, id.toInt(), getDrawableForNotification())
    }

    private fun get(context: Context, position: Int, @DrawableRes drawableRes: Int): Drawable {
        val drawable = ContextCompat.getDrawable(context, drawableRes)!!.mutate() as LayerDrawable
        val gradient = drawable.getDrawable(0) as GradientDrawable

        if (isIconDark){
            val icon = drawable.getDrawable(1) as Drawable
            DrawableCompat.setTint(icon, 0xFF262626.toInt())
        }

        val pos = (position) % COLORS.size
        gradient.colors = COLORS[Math.abs(pos)]
        return drawable
    }

    @DrawableRes
    private fun getDrawable(source: Int): Int {
        when (source) {
            0 -> return R.drawable.placeholder_folder
            1 -> return R.drawable.placeholder_playlist
            2 -> return R.drawable.placeholder_musical_note
            3 -> return R.drawable.placeholder_album
            4 -> return R.drawable.placeholder_artist
            5 -> return R.drawable.placeholder_genre
        }
        throw IllegalArgumentException("invalid source $source")
    }

    @DrawableRes
    private fun getDrawableForNotification(): Int = R.drawable.placeholder_musical_note_for_notification

}