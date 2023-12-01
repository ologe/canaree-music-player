package dev.olog.shared.compose

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "light theme",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    group = "theme"
)
@Preview(
    name = "dark theme",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "theme"
)
annotation class ThemePreviews

@Preview(
    name = "scale 0.75",
    fontScale = .75f,
    group = "font scale",
)
@Preview(
    name = "scale 1",
    fontScale = 1f,
    group = "font scale",
)
@Preview(
    name = "scale 1.25",
    fontScale = 1.25f,
    group = "font scale",
)
annotation class FontScalePreviews