package dev.olog.shared.compose.list

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.R
import dev.olog.shared.compose.Theme
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.DottedDivider

@Composable
fun ListItemHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Column(modifier.padding(start = 12.dp, end = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        top = 12.dp,
                        bottom = if (isSystemInDarkTheme()) 12.dp else 8.dp,
                    )
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Black,
                    color = Theme.textColorPrimary,
                )
                subtitle?.let {
                    Text(
                        text = it,
                        fontSize = with(LocalDensity.current) { dimensionResource(id = R.dimen.item_header_sec_text_size).toSp() },
                        color = Theme.accentColor,
                    )
                }
            }
            trailingContent?.let {
                CompositionLocalProvider(
                    LocalTextStyle provides LocalTextStyle.current.copy(
                        fontSize = with(LocalDensity.current) { dimensionResource(R.dimen.item_header_sec_text_size).toSp() },
                        color = Theme.accentColor,
                    ),
                    LocalContentColor provides Theme.iconColor,
                    content = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            it()
                        }
                    }
                )
            }
        }
        DottedDivider(Modifier.padding(bottom = 8.dp))
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Column(Modifier.background(MaterialTheme.colors.background)) {
            ListItemHeader(title = "Header New")
            ListItemHeader(title = "Header New") {
                Text(text = "trailing")
            }
            ListItemHeader(title = "Header New") {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null,
                    )
                }
            }
            ListItemHeader(
                title = "Header New",
                subtitle = "subtitle"
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null,
                    )
                }
            }
            ListItemHeader(title = "Header Looooooooooooooooooooooooooooooooong")
        }
    }
}