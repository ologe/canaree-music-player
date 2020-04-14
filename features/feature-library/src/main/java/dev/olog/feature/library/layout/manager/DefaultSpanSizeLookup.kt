package dev.olog.feature.library.layout.manager

import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.feature.library.SpanCountController

internal class DefaultSpanSizeLookup(
    var requestedSpanSize: Int
) : GridLayoutManager.SpanSizeLookup() {

    fun getSpanCount() = SpanCountController.SPAN_COUNT

    override fun getSpanSize(position: Int): Int {
        return getSpanCount() / requestedSpanSize
    }
}