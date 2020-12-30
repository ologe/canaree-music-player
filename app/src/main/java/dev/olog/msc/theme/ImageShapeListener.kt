package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.msc.R
import dev.olog.shared.ConflatedSharedFlow
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.android.theme.ImageShapeAmbient
import dev.olog.shared.value
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ImageShapeListener @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences
) : BaseThemeUpdater(
    key = context.getString(R.string.prefs_icon_shape_key)
), ImageShapeAmbient {

    private val _publisher = ConflatedSharedFlow(fetchValue())

    init {
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override val value: ImageShape
        get() = _publisher.value
    override val flow: Flow<ImageShape>
        get() = _publisher

    override fun onPrefsChanged() {
        _publisher.tryEmit(fetchValue())
    }

    private fun fetchValue(): ImageShape {
        val value = prefs.getString(key, context.getString(R.string.prefs_icon_shape_rounded))

        return when (value) {
            context.getString(R.string.prefs_icon_shape_rounded) -> ImageShape.ROUND
            context.getString(R.string.prefs_icon_shape_square) -> ImageShape.RECTANGLE
            context.getString(R.string.prefs_icon_shape_cut_corner) -> ImageShape.CUT_CORNER
            else -> error("image shape not valid=$value")
        }
    }

}