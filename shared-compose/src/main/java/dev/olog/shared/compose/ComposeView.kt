package dev.olog.shared.compose

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import dev.olog.shared.compose.theme.CanareeTheme

fun ComposeView(
    fragment: Fragment,
    content: @Composable () -> Unit
): ComposeView {
    return ComposeView(fragment.requireContext()).apply {
        setContent {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            CanareeTheme(content = content)
        }
    }
}

@Composable
fun ComposeAndroidView(
    id: Int = rememberSaveable { View.generateViewId() },
    content: @Composable (View) -> Unit
) {
    AndroidView(
        factory = { ComposeView(it).apply { this.id = id } },
        update = {
            it.setContent {
                CanareeTheme {
                    content(it)
                }
            }
        }
    )
}