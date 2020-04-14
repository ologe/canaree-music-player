package dev.olog.feature.library.layout.manager

import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.feature.library.SpanCountController

internal abstract class AbsSpanSizeLookup(
    var requestedSpanSize: Int
) : GridLayoutManager.SpanSizeLookup() {

    fun getSpanCount() = SpanCountController.SPAN_COUNT

}