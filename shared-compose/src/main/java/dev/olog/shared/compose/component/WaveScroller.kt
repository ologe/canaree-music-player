package dev.olog.shared.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.olog.shared.compose.theme.Theme
import dev.olog.shared.widgets.scroller.RxWaveSideBarView
import dev.olog.shared.widgets.scroller.WaveSideBarView

@Composable
fun WaveScroller(
    letters: List<String>,
    listener: WaveSideBarView.OnTouchLetterChangeListener,
    modifier: Modifier = Modifier,
    showFade: Boolean = true,
) {
    Box(
        modifier = modifier.width(IntrinsicSize.Max),
        contentAlignment = Alignment.Center,
    ) {
        if (showFade) {
            Spacer(
                Modifier
                    .width(36.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(listOf(Color.Transparent, Theme.colors.surface))
                    )
            )
        }
        AndroidView(
            factory = { RxWaveSideBarView(it) },
            modifier = Modifier.heightIn(max = 420.dp),
        ) {
            it.onLettersChanged(letters)
            it.setListener(listener)
        }
    }
}