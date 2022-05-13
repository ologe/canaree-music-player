package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.platform.theme.ImageShape
import dev.olog.msc.R
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import javax.inject.Inject

internal class ImageShapeListener @Inject constructor(
    @ApplicationContext context: Context,
    prefs: SharedPreferences
) : BaseThemeUpdater<ImageShape>(context, prefs, context.getString(R.string.prefs_icon_shape_key)) {

    val imageShapePublisher by lazy { ConflatedBroadcastChannel(getValue()) }
    fun imageShape() = imageShapePublisher.value

    override fun onPrefsChanged() {
        val imageShape = getValue()
        imageShapePublisher.trySend(imageShape)
    }

    override fun getValue(): ImageShape {
        val value = prefs.getString(key, context.getString(R.string.prefs_icon_shape_rounded))

        return when (value) {
            context.getString(R.string.prefs_icon_shape_rounded) -> ImageShape.ROUND
            context.getString(R.string.prefs_icon_shape_square) -> ImageShape.RECTANGLE
            context.getString(R.string.prefs_icon_shape_cut_corner) -> ImageShape.CUT_CORNER
            else -> throw IllegalArgumentException("image shape not valid=$value")
        }
    }

}