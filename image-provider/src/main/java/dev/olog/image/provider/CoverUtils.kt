package dev.olog.image.provider

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.lib.ColorDesaturationUtils
import dev.olog.shared.extension.isDarkMode
import kotlin.math.abs

object CoverUtils {

    private val COLORS = listOf (
            intArrayOf(0xff_00_c9_ff.toInt(), 0xff_92_fe_9d.toInt()),
            intArrayOf(0xff_f5_4e_a2.toInt(), 0xff_ff_76_76.toInt()),
            intArrayOf(0xff_17_ea_d9.toInt(), 0xff_92_fe_9d.toInt()),
            intArrayOf(0xff_7b_43_97.toInt(), 0xff_dc_24_30.toInt()),
            intArrayOf(0xff_1c_d8_d2.toInt(), 0xff_93_ed_c7.toInt()),
            intArrayOf(0xff_1f_86_ef.toInt(), 0xff_56_41_db.toInt()),
            intArrayOf(0xff_f0_2f_c2.toInt(), 0xff_60_94_ea.toInt()),
            intArrayOf(0xff_00_d2_ff.toInt(), 0xff_3a_7b_d5.toInt()),
            intArrayOf(0xff_f8_57_a6.toInt(), 0xff_ff_58_58.toInt()),
            intArrayOf(0xff_aa_ff_a9.toInt(), 0xff_11_ff_bd.toInt()),
            intArrayOf(0xff_00_c6_ff.toInt(), 0xff_00_72_ff.toInt()),
            intArrayOf(0xff_43_ce_a2.toInt(), 0xff_18_5a_9d.toInt()),
            intArrayOf(0xff_B6_50_DB.toInt(), 0xff_28_73_E1.toInt()),
            intArrayOf(0xff_17_ea_d9.toInt(), 0xff_60_98_ea.toInt()),
            intArrayOf(0xFF_38_ee_7e.toInt(), 0xFF_13_9c_8e.toInt()),
            intArrayOf(0xFF_38_ce_dc.toInt(), 0xFF_5a_89_e5.toInt()),
            intArrayOf(0xFF_15_85_cb.toInt(), 0xFF_2a_36_b3.toInt()),
            intArrayOf(0xFF_99_4f_bb.toInt(), 0xFF_30_34_b3.toInt()),
            intArrayOf(0xFF_83_00_ff.toInt(), 0xFF_dd_00_ff.toInt()),
            intArrayOf(0xFF_df_26_74.toInt(), 0xFF_fe_4f_32.toInt()),
            intArrayOf(0xFF_84_04_81.toInt(), 0xFF_e2_60_92.toInt()),
            intArrayOf(0xFF_ff_60_62.toInt(), 0xFF_ff_96_66.toInt()),
            intArrayOf(0xFF_fc_4e_1b.toInt(), 0xFF_f8_b3_33.toInt()),
            intArrayOf(0xFF_f7_9f_32.toInt(), 0xFF_fc_ca_1c.toInt())
    ).shuffled()

    private val DESATURATED_COLORS by lazy {
        COLORS.map { original ->
            val ints = original.copyOf()
            ints[0] = ColorDesaturationUtils.desaturate(ints[1], .25f, .75f)
            ints[1] = ints[0]
            ints
        }
    }

    fun getGradient(context: Context, mediaId: MediaId): Drawable {
        return getGradient(context, mediaId.resolveId.toInt(), mediaId.resolveSource)
    }

    fun getGradient(context: Context, position: Int, source: Int = 2): Drawable {
        return get(
            context,
            position,
            getDrawable(source)
        )
    }

    fun onlyGradient(context: Context, mediaId: MediaId): Drawable {
        val drawable = ContextCompat.getDrawable(context, getDrawable(mediaId.resolveSource))!!.mutate() as LayerDrawable
        val gradient = drawable.getDrawable(0).mutate() as GradientDrawable

        val position = mediaId.resolveId.toInt()
        if (!context.isDarkMode()) {
            // use custom color for light theme
            val pos = (position) % COLORS.size
            gradient.colors = COLORS[abs(pos)]
        } else {
            val pos = (position) % DESATURATED_COLORS.size
            gradient.colors = DESATURATED_COLORS[abs(pos)]
        }
        return gradient
    }

    private fun get(context: Context, position: Int, @DrawableRes drawableRes: Int): Drawable {
        val drawable = ContextCompat.getDrawable(context, drawableRes)!!.mutate() as LayerDrawable
        val gradient = drawable.getDrawable(0) as GradientDrawable

        if (!context.isDarkMode()) {
            // use custom color for light theme
            val pos = (position) % COLORS.size
            gradient.colors = COLORS[abs(pos)]
        } else {
            val pos = (position) % DESATURATED_COLORS.size
            gradient.colors = DESATURATED_COLORS[abs(pos)]
        }

        return drawable
    }

    @DrawableRes
    private fun getDrawable(source: Int): Int = when (source) {
        MediaIdCategory.FOLDERS.ordinal -> R.drawable.placeholder_folder
        MediaIdCategory.PLAYLISTS.ordinal,
        MediaIdCategory.PODCASTS_PLAYLIST.ordinal -> R.drawable.placeholder_playlist
        MediaIdCategory.SONGS.ordinal -> R.drawable.placeholder_musical_note
        MediaIdCategory.ALBUMS.ordinal,
        MediaIdCategory.PODCASTS_ALBUMS.ordinal -> R.drawable.placeholder_album
        MediaIdCategory.ARTISTS.ordinal,
        MediaIdCategory.PODCASTS_ARTISTS.ordinal -> R.drawable.placeholder_artist
        MediaIdCategory.GENRES.ordinal -> R.drawable.placeholder_genre
        MediaIdCategory.PODCASTS.ordinal -> R.drawable.placeholder_podcast
        else -> throw IllegalArgumentException("invalid source $source")
    }

}