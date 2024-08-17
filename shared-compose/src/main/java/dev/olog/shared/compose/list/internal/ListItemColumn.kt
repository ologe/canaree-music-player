@file:OptIn(ExperimentalFoundationApi::class)

package dev.olog.shared.compose.list.internal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.R
import dev.olog.shared.compose.Theme
import dev.olog.shared.compose.component.Capsule
import dev.olog.shared.compose.component.FixedSizeLayout
import dev.olog.shared.compose.component.rememberScaleIndication

@Composable
internal fun ListItemColumn(
    image: @Composable () -> Unit,
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
) {
    Column(
        modifier = modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                indication = null,
                interactionSource = interactionSource,
            )
            .padding(dimensionResource(R.dimen.item_album_margin)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FixedSizeLayout(
            modifier = Modifier
                .aspectRatio(1f)
                .indication(
                    interactionSource = interactionSource,
                    indication = rememberScaleIndication(),
                ),
            content = image
        )
        Spacer(Modifier.height(6.dp))
        CompositionLocalProvider(
            LocalContentColor provides Theme.colors.textColorPrimary,
            LocalTextStyle provides LocalTextStyle.current.copy(
                fontSize = with(LocalDensity.current) { dimensionResource(R.dimen.item_album_title).toSp() },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
            content = title,
        )
        subtitle?.let {
            CompositionLocalProvider(
                LocalContentColor provides Theme.colors.textColorSecondary,
                LocalTextStyle provides LocalTextStyle.current.copy(
                    fontSize = with(LocalDensity.current) { dimensionResource(R.dimen.item_album_subtitle).toSp() },
                    textAlign = TextAlign.Center,
                ),
                content = it
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Column {
            LazyRow {
                items(5) {
                    ListItemColumn(
                        image = { Capsule(Modifier.fillMaxSize()) },
                        title = { Capsule(
                            Modifier
                                .fillMaxWidth()
                                .height(10.dp)) },
                        subtitle = { Capsule(
                            Modifier
                                .fillMaxWidth()
                                .height(10.dp)) },
                        modifier = Modifier.width(100.dp),
                        onClick = {},
                        onLongClick = {},
                    )
                }
            }
            LazyRow {
                items(5) {
                    ListItemColumn(
                        image = { Capsule(Modifier.fillMaxSize()) },
                        title = { Capsule(
                            Modifier
                                .fillMaxWidth()
                                .height(10.dp)) },
                        subtitle = { Capsule(
                            Modifier
                                .fillMaxWidth()
                                .height(10.dp)) },
                        modifier = Modifier.width(150.dp),
                        onClick = {},
                        onLongClick = {}
                    )
                }
            }
        }
    }
}