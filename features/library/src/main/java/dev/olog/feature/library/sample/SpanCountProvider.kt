package dev.olog.feature.library.sample

import androidx.ui.tooling.preview.PreviewParameterProvider

internal class SpanCountProvider : PreviewParameterProvider<Int> {

    override val values: Sequence<Int>
        get() = sequenceOf(1, 2, 3, 4)
}