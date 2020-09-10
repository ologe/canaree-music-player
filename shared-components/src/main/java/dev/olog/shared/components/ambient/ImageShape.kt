package dev.olog.shared.components.ambient

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import dev.olog.shared.components.R

val ImageShapeAmbient = staticAmbientOf<ImageShape>()

@Composable
fun ProvideImageShapeAmbient(
    initialShape: ImageShape? = null,
    content: @Composable () -> Unit
) {
    val context = ContextAmbient.current

    // TODO use androidx version
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    val key = context.getString(R.string.prefs_icon_shape_key)
    val default = context.getString(R.string.prefs_icon_shape_rounded)

    var shape by remember {
        val initialValue = initialShape ?: prefs.getString(key, default)!!.toIconShape(context)
        mutableStateOf(initialValue)
    }

    onCommit(context) {

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            shape = prefs.getString(key, default)!!.toIconShape(context)
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)

        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }

    }

    Providers(ImageShapeAmbient provides shape) {
        content()
    }

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