package dev.olog.shared.compose.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import dev.olog.shared.compose.R
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.LocalIconColor
import dev.olog.shared.compose.theme.Theme

@Composable
fun Fab(
    @DrawableRes drawableRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        backgroundColor = Theme.colors.primary.enabled,
        contentColor = Theme.colors.onPrimary.enabled,
        content = {
            CompositionLocalProvider(LocalIconColor provides Theme.colors.onPrimary) {
                Icon(painter = painterResource(drawableRes))
            }
        }
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colors.background),
            contentAlignment = Alignment.Center,
        ) {
            Fab(drawableRes = R.drawable.vd_add) {
                
            }
        }
    }
}