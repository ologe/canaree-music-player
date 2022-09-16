package dev.olog.feature.library.api.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import dev.olog.compose.components.CanareeBackground
import dev.olog.compose.CanareeIcons
import dev.olog.compose.ThemePreviews
import dev.olog.compose.modifier.elevation
import dev.olog.compose.composition.local.LocalQuickAction
import dev.olog.compose.theme.CanareeTheme
import dev.olog.core.MediaId
import dev.olog.feature.media.api.LocalMediaProvider
import dev.olog.platform.theme.QuickAction

private val Background = Color(0xDD_f2f2f2)
private val Tint = Color(0xDD_797979)

@Composable
fun QuickAction(
    mediaId: MediaId,
    modifier: Modifier = Modifier
) {
    val quickAction = LocalQuickAction.current
    if (quickAction == QuickAction.NONE) {
        return
    }

    val mediaProvider = LocalMediaProvider

    Box(
        modifier
            .clickable {
                when (quickAction) {
                    QuickAction.NONE -> error("invalid")
                    QuickAction.PLAY -> mediaProvider.playFromMediaId(mediaId, null)
                    QuickAction.SHUFFLE -> mediaProvider.shuffle(mediaId, null)
                }
            }
            .padding(4.dp)
            .elevation(
                elevation = 8.dp,
                color = Background,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            imageVector = when (quickAction) {
                QuickAction.NONE -> error("invalid $quickAction")
                QuickAction.PLAY -> CanareeIcons.Play
                QuickAction.SHUFFLE -> CanareeIcons.Shuffle
            },
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .padding(6.dp),
            colorFilter = ColorFilter.tint(Tint)
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        CanareeBackground {
            Row(
                modifier = Modifier.size(100.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                for (value in QuickAction.values()) {
                    CompositionLocalProvider(LocalQuickAction provides value) {
                        QuickAction(mediaId = MediaId.songId(1))
                    }
                }
            }
        }
    }
}