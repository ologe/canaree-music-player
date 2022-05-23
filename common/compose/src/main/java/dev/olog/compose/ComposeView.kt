package dev.olog.compose

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import dev.olog.compose.theme.CanareeTheme
import androidx.compose.ui.platform.ComposeView as PlatformComposeView

@Suppress("FunctionName")
fun ComposeView(
    context: Context,
    factory: @Composable () -> Unit,
): View {
    return PlatformComposeView(context).apply {
        setContent {
            CanareeTheme {
                factory()
            }
        }
    }
}