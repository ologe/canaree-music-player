package dev.olog.feature.library.tab.layout.manager

import androidx.recyclerview.widget.GridLayoutManager

abstract class AbsSpanSizeLookup(
    var requestedSpanSize: Int
) : GridLayoutManager.SpanSizeLookup() {

    fun getSpanCount() = SpanCountController.SPAN_COUNT

}