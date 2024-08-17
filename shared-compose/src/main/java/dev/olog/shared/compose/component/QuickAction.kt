package dev.olog.shared.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.theme.HasQuickAction
import dev.olog.shared.android.theme.QuickAction
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.R
import dev.olog.shared.compose.Theme

private val BackgroundColor = Color(0xDD_f2f2f2)
private val IconColor = Color(0xFF_797979)

@Composable
fun QuickAction(
    mediaId: MediaId,
    modifier: Modifier = Modifier,
) {
    when (val action = rememberQuickAction()) {
        QuickAction.NONE -> {}
        QuickAction.PLAY -> QuickAction(mediaId, action, modifier)
        QuickAction.SHUFFLE -> QuickAction(mediaId, action, modifier)
    }
}

@Composable
private fun QuickAction(
    mediaId: MediaId,
    action: QuickAction,
    modifier: Modifier = Modifier
) {
    val mediaProvider = if (LocalInspectionMode.current) {
        null
    } else {
        val context = LocalContext.current
        remember(context) { context.findInContext<MediaProvider>() }
    }
    Spacer(
        // TODO clickable
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    when (action) {
                        QuickAction.NONE -> error("invalid")
                        QuickAction.PLAY -> mediaProvider?.playFromMediaId(mediaId, null, null)
                        QuickAction.SHUFFLE -> mediaProvider?.shuffle(mediaId, null)
                    }
                }
            )
            .padding(6.dp)
            .size(dimensionResource(R.dimen.smallShuffleSize))
            .shadow(8.dp, CircleShape)
            .background(BackgroundColor, CircleShape)
            .padding(6.dp)
            .paint(
                painter = when (action) {
                    QuickAction.NONE -> error("invalid")
                    QuickAction.PLAY -> rememberVectorPainter(Icons.Rounded.PlayArrow)
                    QuickAction.SHUFFLE -> rememberVectorPainter(Icons.Rounded.Shuffle)
                },
                sizeToIntrinsics = true,
                colorFilter = ColorFilter.tint(IconColor)
            ),
    )
}

@Composable
fun rememberQuickAction(): QuickAction {
    if (LocalInspectionMode.current) {
        return QuickAction.SHUFFLE
    }
    val context = LocalContext.current
    val hasQuickAction = remember(context) { context.applicationContext.findInContext<HasQuickAction>() }
    return hasQuickAction.observeQuickAction().collectAsState().value
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Row(Modifier.background(Theme.colors.background)) {
            for (value in QuickAction.values()) {
                QuickAction(MediaId.shuffleId(), value)
            }
        }
    }
}