package dev.olog.shared.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import dev.olog.shared.compose.R

val LocalCanareeTypography = staticCompositionLocalOf<CanareeTypography> {
    error("LocalCanareeTypography not set")
}

val LocalTextStyle = compositionLocalOf<TextStyle> {
    error("LocalTextStyle not set")
}

@Immutable
data class CanareeTypography(
    val body: TextStyle,
    val header: TextStyle,
    val trackTitle: TextStyle,
    val trackSubtitle: TextStyle,
    val albumTitle: TextStyle,
    val albumSubtitle: TextStyle,
)

@Composable
internal fun typography(): CanareeTypography {
    return CanareeTypography(
        body = TextStyle(
//            letterSpacing = 0.013.em,
        ),
        header = TextStyle(
            fontSize = 20.dp.toFakeSp(),
            fontWeight = FontWeight.Black,
            letterSpacing = 0.0125.em,
        ),
        trackTitle = TextStyle(
            fontSize = dimensionResource(R.dimen.item_song_title).toFakeSp(),
            letterSpacing = 0.013.em,
        ),
        trackSubtitle = TextStyle(
            fontSize = dimensionResource(R.dimen.item_song_subtitle).toFakeSp(),
        ),
        albumTitle = TextStyle(
            fontSize = dimensionResource(R.dimen.item_album_title).toFakeSp(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = 0.013.em,
        ),
        albumSubtitle = TextStyle(
            fontSize = dimensionResource(R.dimen.item_album_subtitle).toFakeSp(),
            textAlign = TextAlign.Center,
        ),
    )
}

@Composable
@Stable
internal fun Dp.toFakeSp(): TextUnit {
    return with(LocalDensity.current) { this@toFakeSp.toSp() }
}