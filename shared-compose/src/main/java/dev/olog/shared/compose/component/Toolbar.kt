package dev.olog.shared.compose.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import dev.olog.shared.compose.R
import dev.olog.shared.compose.theme.LocalTextStyle
import dev.olog.shared.compose.theme.Theme

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    title: @Composable RowScope.() -> Unit,
    icons: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.toolbar))
            .padding(start = Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.mediumSmall)
        ) {
            CompositionLocalProvider(LocalTextStyle provides Theme.typography.headline2) {
                title()
            }
        }

        icons?.invoke(this)
    }
}