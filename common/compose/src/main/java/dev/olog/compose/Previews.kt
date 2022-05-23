package dev.olog.compose

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@FontScalePreviews
@ThemePreviews
@DevicePreviews
@OrientationPreviews
@LocalePreviews
annotation class CombinedPreviews

@Preview(
    name = "pixel",
    device = Devices.PIXEL,
    group = "devices",
)
@Preview(
    name = "pixel 4",
    device = Devices.PIXEL_4,
    group = "devices"
)
@Preview(
    name = "pixel 4 xl",
    device = Devices.PIXEL_4_XL,
    group = "devices"
)
@Preview(
    name = "tablet",
    device = Devices.PIXEL_C,
    group = "devices"
)
@Preview(
    name = "foldable",
    device = Devices.FOLDABLE,
    group = "devices"
)
annotation class DevicePreviews

@Preview(
    name = "light theme",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    group = "themes",
)
@Preview(
    name = "dark theme",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "themes",
)
annotation class ThemePreviews

@Preview(
    name = "scale 0.75",
    fontScale = 0.75f,
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

@Preview(
    name = "portrait",
    group = "orientation",
)
@Preview(
    name = "landscape",
    device = Devices.AUTOMOTIVE_1024p,
    widthDp = 720,
    heightDp = 360,
    group = "orientation",
)
annotation class OrientationPreviews

@Preview(
    name = "english",
    group = "locale",
)
@Preview(
    name = "czech",
    locale = "cs",
    group = "locale",
)
@Preview(
    name = "greek",
    locale = "el",
    group = "locale",
)
@Preview(
    name = "spanish",
    locale = "es",
    group = "locale",
)
//@Preview( todo AS is crashing
//    name = "hind",
//    locale = "hi",
//    group = "locale",
//)
@Preview(
    name = "indonesian",
    locale = "id",
    group = "locale",
)
@Preview(
    name = "italian",
    locale = "it",
    group = "locale",
)
@Preview(
    name = "portuguese",
    locale = "pt",
    group = "locale",
)
@Preview(
    name = "vietnamese",
    locale = "vi",
    group = "locale",
)
@Preview(
    name = "chinese",
    locale = "zh-rCN",
    group = "locale",
)
annotation class LocalePreviews