package dev.olog.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olog.compose.CanareeIcons
import dev.olog.compose.FontScalePreviews
import dev.olog.compose.ThemePreviews
import dev.olog.compose.WithMediumEmphasys
import dev.olog.compose.modifier.cursor
import dev.olog.compose.theme.CanareeTheme

private val LightBackground = Color(0xff_f1f3f4)
private val DarkBackground = Color(0xff_303336)

@Composable
fun CanareeSearchBox(
    value: String,
    hint: String,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    onValueChange: (String) -> Unit,
    onClearTextClick: () -> Unit,
) {
    val color = if (isSystemInDarkTheme()) DarkBackground else LightBackground
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(color),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CanareeIcon(CanareeIcons.Search)

        val textStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp,
        )

        var hasFocus by remember { mutableStateOf(false) }
        val colorColor = MaterialTheme.colors.secondary
        val brush = remember { SolidColor(colorColor) }

        val inputService = LocalTextInputService.current

        BasicTextField(
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { hasFocus = it.hasFocus }
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = textStyle.copy(
                color = LocalContentColor.current,
            ),
            cursorBrush = brush,
            decorationBox = {
                DecorationBox(
                    value = value,
                    hint = hint,
                    textStyle = textStyle,
                    hasFocus = hasFocus,
                    brush = brush,
                    actualTextField = it
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = false,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { inputService?.hideSoftwareKeyboard() }
            ),
        )

        // close button
        AnimatedVisibility(value.isNotEmpty()) {
            CanareeIcon(
                imageVector = CanareeIcons.Close,
                modifier = Modifier.clickable(onClick = onClearTextClick),
            )
        }
    }
}

@Composable
private fun DecorationBox(
    value: String,
    hint: String,
    textStyle: TextStyle,
    hasFocus: Boolean,
    brush: Brush,
    actualTextField: @Composable () -> Unit,
) {
    if (value.isEmpty()) {
        WithMediumEmphasys {
            Text(
                text = hint,
                style = textStyle,
                modifier = Modifier.cursor(
                    hasFocus = hasFocus,
                    brush = brush,
                )
            )
        }
    } else {
        actualTextField()
    }
}

@ThemePreviews
@FontScalePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        CanareeBackground(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CanareeSearchBox(
                    value = "Value",
                    hint = "",
                    onValueChange = { },
                    onClearTextClick = { },
                )
                CanareeSearchBox(
                    value = "",
                    hint = "Hint",
                    onValueChange = { },
                    onClearTextClick = { },
                )
            }
        }
    }
}