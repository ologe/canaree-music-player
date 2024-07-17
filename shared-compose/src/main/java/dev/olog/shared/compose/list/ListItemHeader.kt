package dev.olog.shared.compose.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.DottedDivider

@Composable
fun ListItemHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(start = 12.dp)) {
        Text(
            text = title,
            modifier = Modifier
                .padding(
                    top = 12.dp,
                    bottom = 8.dp, // todo 12 dp on dark mode?
                ),
            fontSize = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Black,
        )
        DottedDivider(Modifier.padding(bottom = 8.dp))
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Column(Modifier.background(MaterialTheme.colors.background)) {
            ListItemHeader(title = "Header New")
            ListItemHeader(title = "Header Looooooooooooooooooooooooooooooooong")
        }
    }
}