package dev.olog.shared.components.item

import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope.gravity
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.shared.components.SingleLineText
import dev.olog.shared.components.ambient.CustomTheming
import dev.olog.shared.components.ambient.ImageShape
import dev.olog.shared.components.ambient.QuickAction
import dev.olog.shared.components.image.GlideView
import dev.olog.shared.components.sample.ImageShapePreviewProvider
import dev.olog.shared.components.sample.MediaIdCategoryPreviewProvider
import dev.olog.shared.components.sample.QuickActionPreviewProvider
import dev.olog.shared.components.theme.CanareeTheme
import dev.olog.shared.exhaustive

@Preview(widthDp = 200, group = "Category")
@Composable
private fun ListItemAlbumCategoryPreview(
    @PreviewParameter(MediaIdCategoryPreviewProvider::class) category: MediaIdCategory
) {
    CanareeTheme {
        Surface {
            val mediaId = MediaId.Category(category, "test")
            ListItemAlbum(mediaId, "Get Rich or Die Tryin", "50 Cent")
        }
    }
}

@Preview(widthDp = 200, group = "Shape")
@Composable
private fun ListItemAlbumShapePreview(
    @PreviewParameter(ImageShapePreviewProvider::class) shape: ImageShape
) {
    CanareeTheme(shapeOverride = shape) {
        Surface {
            val mediaId = MediaId.Category(MediaIdCategory.ALBUMS, "test")
            ListItemAlbum(mediaId, "Get Rich or Die Tryin", "50 Cent")
        }
    }
}

@Preview(widthDp = 200, group = "Quick action")
@Composable
private fun ListItemAlbumQuickActionPreview(
    @PreviewParameter(QuickActionPreviewProvider::class) action: QuickAction
) {
    CanareeTheme(quickActionOverride = action) {
        Surface {
            val mediaId = MediaId.Category(MediaIdCategory.ALBUMS, "test")
            ListItemAlbum(mediaId, "Get Rich or Die Tryin", "50 Cent")
        }
    }
}

/**
 * @param subtitle if blank, the text view will not be displayed
 */
@Composable
fun ListItemAlbum(
    mediaId: MediaId.Category,
    title: String,
    subtitle: String,
    shape: Shape = CustomTheming.imageShape
) {

    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
    ) {
        Stack {
            GlideView(
                mediaId = mediaId,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                shape = shape
            )
            QuickAction(Modifier.gravity(Alignment.BottomEnd))
        }
        Spacer(modifier = Modifier.height(4.dp))
        SingleLineText(
            text = title,
            maxLines = 2,
            align = TextAlign.Center
        )
        ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
            SingleLineText(
                subtitle,
                style = MaterialTheme.typography.body2,
                align = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickAction(modifier: Modifier = Modifier) {
    val action = CustomTheming.quickAction
    when (action) {
        QuickAction.NONE -> {}
        QuickAction.PLAY -> QuickActionContent(modifier, Icons.Rounded.PlayArrow)
        QuickAction.SHUFFLE -> QuickActionContent(modifier, Icons.Rounded.Shuffle)
    }.exhaustive
}

@Composable
private fun QuickActionContent(
    modifier: Modifier,
    asset: VectorAsset
) {
    Box(modifier = Modifier
        .padding(8.dp)
        .clip(CircleShape)
        .drawShadow(8.dp)
        .size(30.dp)
        .background(Color(0xDDf2f2f2)) // TODO hardcoded
        .then(modifier),
        gravity = ContentGravity.Center
    ) {
        Icon(
            asset = asset,
            modifier = Modifier.padding(6.dp),
            tint = Color(0xFF_797979) // TODO hardcoded
        )
    }
}