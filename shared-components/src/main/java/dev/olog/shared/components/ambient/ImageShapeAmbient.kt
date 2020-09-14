package dev.olog.shared.components.ambient

import android.content.Context
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.olog.shared.components.R

val ImageShapeAmbient = staticAmbientOf<ImageShape>()

@Composable
internal fun ProvideImageShapeAmbient(
    override: ImageShape? = null,
    content: @Composable () -> Unit
) {
    val context = ContextAmbient.current
    SharedPreferenceAmbient(
        key = stringResource(R.string.prefs_icon_shape_key),
        default = stringResource(R.string.prefs_icon_shape_rounded),
        override = override,
        mapper = { it.toIconShape(context) },
        content = {
            Providers(ImageShapeAmbient provides it) {
                content()
            }
        }
    )

}

enum class ImageShape {
    RECTANGLE, ROUND, CUT_CORNER
}

val ImageShape.shape: Shape
    get() = when(this) {
        ImageShape.RECTANGLE -> RectangleShape
        ImageShape.ROUND -> RoundedCornerShape(8.dp)
        ImageShape.CUT_CORNER -> CutCornerShape(8.dp)
    }

private fun String.toIconShape(context: Context): ImageShape = when (this) {
    context.getString(R.string.prefs_icon_shape_rounded) -> ImageShape.ROUND
    context.getString(R.string.prefs_icon_shape_square) -> ImageShape.RECTANGLE
    context.getString(R.string.prefs_icon_shape_cut_corner) -> ImageShape.CUT_CORNER
    else -> {
        // TODO log and fallback to default
        ImageShape.ROUND
    }
}