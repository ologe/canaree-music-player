package dev.olog.feature.player.api.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olog.compose.Background
import dev.olog.compose.ThemePreviews
import dev.olog.compose.theme.CanareeTheme

@Composable
fun NowPlaying(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val alphaValue by animateFloatAsState(if (isSelected) LocalContentAlpha.current else .4f)
    Box(
        modifier = modifier.height(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(localization.R.string.player_now_playing),
            style = MaterialTheme.typography.h5,
            fontSize = 28.sp,
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer {
                alpha = alphaValue
            }
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Background {
            Column {
                NowPlaying(true)
                NowPlaying(false)
            }
        }
    }
}