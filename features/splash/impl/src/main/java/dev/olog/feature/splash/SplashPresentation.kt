package dev.olog.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import dev.olog.compose.components.CanareeBackground
import dev.olog.compose.CombinedPreviews
import dev.olog.compose.theme.CanareeTheme

@Composable
fun SplashPresentation(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            imageVector = ImageVector.vectorResource(id = dev.olog.ui.R.drawable.vd_bird_singing),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
        )

        Spacer(modifier = Modifier.weight(.5f))

        Text(
            text = "Canaree",
            style = MaterialTheme.typography.h4,
        )
        Spacer(modifier = Modifier.weight(.5f))
    }
}

@CombinedPreviews
@Composable
private fun Preview() {
    CanareeTheme {
        CanareeBackground {
            SplashPresentation(Modifier.fillMaxSize())
        }
    }
}
