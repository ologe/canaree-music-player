package dev.olog.compose.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

internal val Typography = TypographyUtils.fromDefaults(
    h4 = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 40.sp,
        letterSpacing = 0.sp,
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        letterSpacing = 0.1.sp,
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.15.sp,
    ),
    subtitle2 = TextStyle(
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.15.sp,
    ),
    body1 = TextStyle(
        fontSize = 16.sp,
        letterSpacing = 0.sp,
    ),
    body2 = TextStyle(
        letterSpacing = 0.sp,
    ),
    button = TextStyle(
        letterSpacing = 0.2.sp,
    ),
    caption = TextStyle(
        letterSpacing = 0.sp,
    ),
    overline = TextStyle(
        letterSpacing = 0.5.sp,
    ),
)

@Preview(showBackground = true)
@Composable
private fun Preview() {
    CanareeTheme {
        Column(Modifier.fillMaxWidth()) {
//            Text( too big for mobile
//                text = "Headline 1",
//                style = MaterialTheme.typography.h1
//            )
//            Text(
//                text = "Headline 2",
//                style = MaterialTheme.typography.h2
//            )
//            Text(
//                text = "Headline 3",
//                style = MaterialTheme.typography.h3
//            )
            Text(
                text = "Headline 4",
                style = MaterialTheme.typography.h4
            )
            Text(
                text = "Headline 5",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "Headline 6",
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "Subtitle 1",
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = "Subtitle 2",
                style = MaterialTheme.typography.subtitle2
            )
            Text(
                text = "Body 1",
                style = MaterialTheme.typography.body1
            )
            Text(
                text = "Body 2",
                style = MaterialTheme.typography.body2
            )
            Text(
                text = "Button",
                style = MaterialTheme.typography.button
            )
            Text(
                text = "Caption",
                style = MaterialTheme.typography.caption
            )
            Text(
                text = "Overline",
                style = MaterialTheme.typography.overline
            )
        }
    }
}