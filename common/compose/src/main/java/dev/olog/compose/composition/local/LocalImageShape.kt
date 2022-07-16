package dev.olog.compose.composition.local

import android.content.Context
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.olog.feature.settings.api.R
import dev.olog.platform.theme.ImageShape

val LocalImageShape = staticCompositionLocalOf<ImageShape> { error("LocalImageShape not set") }

@Composable
fun ProvideImageShapePrefs(
    override: ImageShape? = null,
    content: @Composable () -> Unit
) {
    LocalPreference(
        key = stringResource(R.string.prefs_icon_shape_key),
        serialize = { it.toPref(this) },
        deserialize = { mapValue(this, it) },
        default = ImageShape.ROUND,
        override = override,
        providableCompositionLocal = LocalImageShape,
        content = content,
    )
}

private fun mapValue(
    context: Context,
    value: String
): ImageShape {
    return when (value) {
        context.getString(dev.olog.feature.settings.api.R.string.prefs_icon_shape_rounded) -> ImageShape.ROUND
        context.getString(dev.olog.feature.settings.api.R.string.prefs_icon_shape_square) -> ImageShape.RECTANGLE
        context.getString(dev.olog.feature.settings.api.R.string.prefs_icon_shape_cut_corner) -> ImageShape.CUT_CORNER
        else -> ImageShape.ROUND
    }
}

private fun ImageShape.toPref(context: Context): String {
    val key = when (this) {
        ImageShape.RECTANGLE -> dev.olog.feature.settings.api.R.string.prefs_icon_shape_square
        ImageShape.ROUND -> dev.olog.feature.settings.api.R.string.prefs_icon_shape_rounded
        ImageShape.CUT_CORNER -> dev.olog.feature.settings.api.R.string.prefs_icon_shape_cut_corner
    }
    return context.getString(key)
}

private val RoundedShape = RoundedCornerShape(6.dp)
private val CutCornerShape = CutCornerShape(6.dp)

fun ImageShape.toComposeShape(): Shape = when (this) {
    ImageShape.RECTANGLE -> RectangleShape
    ImageShape.ROUND -> RoundedShape
    ImageShape.CUT_CORNER -> CutCornerShape
}