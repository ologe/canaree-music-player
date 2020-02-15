package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import dev.olog.core.dagger.ApplicationContext
import dev.olog.presentation.R
import dev.olog.shared.android.theme.ImageShape
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

internal class ImageShapeListener @Inject constructor(
    @ApplicationContext context: Context,
    prefs: SharedPreferences
) : BaseThemeUpdater<ImageShape>(context, prefs, context.getString(R.string.prefs_icon_shape_key)) {

    private val imageShapePublisher by lazy {
        ConflatedBroadcastChannel(getValue())
    }

    fun imageShape() = imageShapePublisher.value
    fun observeImageShape(): Flow<ImageShape> {
        return imageShapePublisher.asFlow()
    }

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