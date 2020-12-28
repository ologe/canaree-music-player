package dev.olog.feature.library.tab.span

import androidx.recyclerview.widget.GridLayoutManager

abstract class AbsSpanSizeLookup(
    var requestedSpanSize: Int
) : GridLayoutManager.SpanSizeLookup() {

    fun getSpanCount() = TabFragmentSpanCountController.SPAN_COUNT

}