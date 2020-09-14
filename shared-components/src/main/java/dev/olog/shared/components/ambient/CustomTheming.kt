package dev.olog.shared.components.ambient

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape

object CustomTheming {

    @Composable
    val imageShape: Shape
        get() = ImageShapeAmbient.current.shape

    @Composable
    val quickAction: QuickAction
        get() = QuickActionAmbient.current

}

@Composable
fun ProvideAmbients(
    shapeOverride: ImageShape? = null,
    quickActionOverride: QuickAction? = null,
    content: @Composable () -> Unit
) {
    ProvideDisplayInsets {
        ProvideImageShapeAmbient(shapeOverride) {
            ProvideQuickActionAmbient(quickActionOverride) {
                content()
            }
        }
    }
}