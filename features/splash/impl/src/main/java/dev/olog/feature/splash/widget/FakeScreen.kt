package dev.olog.feature.splash.widget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.olog.compose.Background
import dev.olog.compose.DevicePreviews
import dev.olog.compose.OrientationPreviews
import dev.olog.compose.ThemePreviews
import dev.olog.compose.glide.Image
import dev.olog.compose.statusBarsPadding
import dev.olog.compose.theme.CanareeTheme
import dev.olog.core.MediaId
import dev.olog.feature.player.api.widget.NowPlaying
import dev.olog.feature.player.api.widget.Switcher
import dev.olog.feature.player.api.widget.SwitcherDirection
import dev.olog.feature.player.api.widget.swipeableModifier

private val CornerSize = 12.dp

@Composable
fun FakePhone(
    modifier: Modifier = Modifier,
    paddingTop: Dp = 24.dp,
    maxWidth: Dp = 500.dp,
    disallowParentInterceptEvent: (Boolean) -> Unit = { },
) {
    Box(modifier) {
        val resource = painterResource(id = dev.olog.feature.splash.R.drawable.phone_black)
        val ratio = resource.intrinsicSize.width / resource.intrinsicSize.height

        // phone image
        Image(
            painter = resource,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .statusBarsPadding(plus = paddingTop)
                .widthIn(max = maxWidth)
                .aspectRatio(ratio)
        )

        // fake frame
        ScreenFrame(
            modifier = Modifier
                .matchParentSize()
                .padding(top = paddingTop)
                .widthIn(max = maxWidth)
        ) {
            // fake screen
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = CornerSize, topEnd = CornerSize))
                    .background(MaterialTheme.colors.background)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var isSelected by remember { mutableStateOf(false) }

                NowPlaying(
                    isSelected = isSelected,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

                SwipeableView(
                    isSelected = isSelected,
                    onSwipeLeft = {
                        isSelected = true
                    },
                    onSwipeRight = {
                        isSelected = true
                    },
                    onClick = {
                        isSelected = !isSelected
                    },
                    disallowParentInterceptEvent = disallowParentInterceptEvent,
                )
            }
        }
    }
}

@Composable
private fun SwipeableView(
    isSelected: Boolean,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onClick: () -> Unit,
    disallowParentInterceptEvent: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var direction by remember { mutableStateOf(SwitcherDirection.None) }
    var progressive by remember { mutableStateOf(0L) }
    val coverElevation by animateDpAsState(if (isSelected) 8.dp else 0.dp)

    Box(
        modifier = modifier
            .swipeableModifier(
                onSwipeLeft = {
                    progressive++
                    onSwipeLeft()
                    direction = SwitcherDirection.Next
                },
                onSwipeRight = {
                    progressive--
                    onSwipeRight()
                    direction = SwitcherDirection.Previous
                },
                onClick = onClick,
                disallowParentInterceptEvent = disallowParentInterceptEvent,
            )
    ) {
        Switcher(
            mediaId = MediaId.songId(progressive),
            direction = direction,
        ) { mediaId ->
            Image(
                mediaId = mediaId,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    // todo enable colored shadow?
                    // todo shadow is broken when clip(shape) is applied
                    .clip(RoundedCornerShape(8.dp))
                    .shadow(coverElevation, clip = true),
            )
        }
    }
}

@Composable
private fun ScreenFrame(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    // use weights to maintain space proportion between screen sizes
    val topSpaceWeight = .2f
    val leftSpaceWeight = .06f
    val rightSpaceWeight = .08f

    Column(modifier) {
        Spacer(modifier = Modifier.weight(topSpaceWeight)) // top space
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.weight(leftSpaceWeight)) // left space

            // image
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                content()
            }

            Spacer(modifier = Modifier.weight(rightSpaceWeight)) // right space
        }
    }
}

@DevicePreviews
@OrientationPreviews
@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Background(Modifier.fillMaxSize()) {
            FakePhone(Modifier.align(Alignment.BottomEnd))
        }
    }
}