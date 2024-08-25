package dev.olog.shared.compose.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.olog.core.MediaId
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.theme.HasImageShape
import dev.olog.shared.android.theme.ImageShape

private val RoundedCorners = RoundedCornerShape(5.dp)
private val CutCorners = CutCornerShape(5.dp)

fun Modifier.shaped(mediaId: MediaId): Modifier = this.composed {
    val context = LocalContext.current
    val shape = if (mediaId.isAnyArtist && !mediaId.isLeaf) {
        CircleShape
    } else if (LocalInspectionMode.current) {
        RoundedCorners
    } else {
        val hasImageShape = remember(context) {
            context.applicationContext.findInContext<HasImageShape>()
        }
        val imageShape by hasImageShape.observeImageShape().collectAsState()
        when (imageShape) {
            ImageShape.RECTANGLE -> RectangleShape
            ImageShape.ROUND -> RoundedCorners
            ImageShape.CUT_CORNER -> CutCorners
        }
    }
    Modifier.clip(shape)
}