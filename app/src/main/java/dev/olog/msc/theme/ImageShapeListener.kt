package dev.olog.msc.theme

import android.app.Application
import android.content.SharedPreferences
import dev.olog.presentation.R
import dev.olog.shared.android.theme.ImageShape
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import javax.inject.Inject

internal class ImageShapeListener @Inject constructor(
    application: Application,
    prefs: SharedPreferences
) : BaseThemeUpdater<ImageShape>(application, prefs, application.getString(R.string.prefs_icon_shape_key)) {

    val imageShapePublisher by lazy { ConflatedBroadcastChannel(getValue()) }
    fun imageShape() = imageShapePublisher.value

    override fun onPrefsChanged() {
        val imageShape = getValue()
        imageShapePublisher.offer(imageShape)
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