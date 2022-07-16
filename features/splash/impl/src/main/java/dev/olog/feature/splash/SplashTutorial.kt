package dev.olog.feature.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.olog.compose.components.CanareeBackground
import dev.olog.compose.CombinedPreviews
import dev.olog.compose.isLargeScreen
import dev.olog.compose.theme.CanareeTheme
import dev.olog.feature.splash.widget.FakePhone

@Composable
fun SplashTutorial(
    modifier: Modifier = Modifier,
    disallowParentInterceptEvent: (Boolean) -> Unit,
) {
    if (isLargeScreen) {
        SplashTutorialLandscape(
            modifier = modifier,
            disallowParentInterceptEvent = disallowParentInterceptEvent
        )
    } else {
        SplashTutorialPortrait(
            modifier = modifier,
            disallowParentInterceptEvent = disallowParentInterceptEvent
        )
    }
}

@Composable
private fun SplashTutorialPortrait(
    modifier: Modifier = Modifier,
    disallowParentInterceptEvent: (Boolean) -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(2f))
        TutorialText()
        Spacer(modifier = Modifier.weight(.1f))
        FakePhone(
            disallowParentInterceptEvent = disallowParentInterceptEvent
        )
    }
}

@Composable
private fun SplashTutorialLandscape(
    modifier: Modifier = Modifier,
    disallowParentInterceptEvent: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            TutorialText()
        }
        Box(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            FakePhone(
                disallowParentInterceptEvent = disallowParentInterceptEvent
            )
        }
    }
}

@Composable
private fun TutorialText(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(id = localization.R.string.splash_player_controls),
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(id = localization.R.string.splash_player_tutorial_1),
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(id = localization.R.string.splash_player_tutorial_2),
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
        )
    }
}

@CombinedPreviews
@Composable
private fun Preview() {
    CanareeTheme {
        CanareeBackground {
            SplashTutorial(
                modifier = Modifier.fillMaxSize(),
                disallowParentInterceptEvent = { }
            )
        }
    }
}
