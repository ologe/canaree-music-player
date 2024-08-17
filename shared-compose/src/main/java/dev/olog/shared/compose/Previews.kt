package dev.olog.shared.compose

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO and Configuration.UI_MODE_NIGHT_MASK)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES and Configuration.UI_MODE_NIGHT_MASK)
annotation class ThemePreviews