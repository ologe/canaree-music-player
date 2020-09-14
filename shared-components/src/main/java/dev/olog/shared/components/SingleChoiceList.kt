package dev.olog.shared.components

import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import dev.olog.shared.components.theme.CanareeTheme

@Preview
@Composable
private fun SingleChoiceListPreview() {
    CanareeTheme {
        SingleChoiceList(
            items = listOf("Folders", "Playlists", "Tracks", "Albums", "Artists", "Genres"),
            selected = "Albums"
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES) // TODO not working, preview is showing incorrectly
@Composable
private fun SingleChoiceListDarkModePreview() {
    CanareeTheme(darkTheme = true) { // TODO remove this after fixing preview
        SingleChoiceList(
            items = listOf("Folders", "Playlists", "Tracks", "Albums", "Artists", "Genres"),
            selected = "Albums"
        )
    }
}

@Composable
fun<T> SingleChoiceList(
    items: List<T>,
    selected: T,
    text: @Composable (T) -> String = { it.toString() },
    onClick: (T) -> Unit = {}
) {
    Surface(modifier = Modifier.fillMaxWidth()) {
        LazyColumnFor(items = items) {
            val textColor = if (it == selected) {
                if (isSystemInDarkTheme()) colors.onSecondary else colors.secondary
            } else {
                colors.onSurface
            }
            val backgroundColor = if (it == selected) colors.secondary.copy(alpha = .3f) else Color.Transparent

            val shapeSize = 32.dp
            val outerPadding = 4.dp
            Box(
                modifier = Modifier
                    .padding(top = outerPadding, bottom = outerPadding, end = outerPadding)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topRight = shapeSize, bottomRight = shapeSize))
                    .background(backgroundColor)
                    .clickable {
                        onClick(it)
                    }
            ) {
                Text(
                    text = text(it),
                    color = textColor,
                    style = MaterialTheme.typography.h6 ,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}