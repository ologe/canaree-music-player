package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import dev.olog.core.dagger.ApplicationContext
import dev.olog.presentation.R
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.theme.ImageShape
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class ImageShapeListener @Inject constructor(
        @ApplicationContext context: Context,
        prefs: SharedPreferences
) : BaseThemeUpdater(context, prefs, context.getString(R.string.prefs_icon_shape_key)){

    val imageShapePublisher by lazyFast { ConflatedBroadcastChannel<ImageShape>() }
    fun imageShape() = imageShapePublisher.value

    override fun onPrefsChanged(forced: Boolean) {
        val value = prefs.getString(key, context.getString(R.string.prefs_icon_shape_rounded))

        val imageShape = when (value) {
            context.getString(R.string.prefs_icon_shape_rounded) -> ImageShape.ROUND
            context.getString(R.string.prefs_icon_shape_square) -> ImageShape.RECTANGLE
            else -> throw IllegalArgumentException("image shape not valid=$value")
        }
        GlobalScope.launch { imageShapePublisher.send(imageShape) }
    }

}