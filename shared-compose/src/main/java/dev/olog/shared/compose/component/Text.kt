package dev.olog.shared.compose.component

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import dev.olog.shared.compose.theme.LocalContentColor
import dev.olog.shared.compose.theme.LocalTextStyle

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = LocalContentColor.current.enabled,
    fontWeight: FontWeight? = style.fontWeight,
    fontSize: TextUnit = style.fontSize,
    textAlign: TextAlign? = style.textAlign,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy( // TODO change with merge when upgrading to latest compose?
            color = color,
            fontWeight = fontWeight,
            fontSize = fontSize,
            textAlign = textAlign,
        ),
        overflow = overflow,
        maxLines = maxLines,
    )
}