package dev.olog.shared.compose.component

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import dev.olog.core.MediaId
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.theme.HasImageShape
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.compose.shape.toShape

private val RoundedCorners = RoundedCornerShape(12.dp)
private val CutCorners = CutCornerShape(5.dp)
private val ArtistShape = RoundedPolygon.star(
    numVerticesPerRadius = 15,
    radius = 1.11f,
    innerRadius = 1f,
    rounding = CornerRounding(
        radius = 1f,
        smoothing = 0f
    )
).toShape()

fun Modifier.shaped(mediaId: MediaId): Modifier = this.composed {
    val context = LocalContext.current
    val shape = if (mediaId.isAnyArtist && !mediaId.isLeaf) {
        ArtistShape
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