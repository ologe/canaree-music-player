package dev.olog.msc.presentation.library.tab.span.size.lookup

import android.support.v7.widget.GridLayoutManager

abstract class AbsSpanSizeLookup : GridLayoutManager.SpanSizeLookup() {

    protected val spanCount = 60

    fun getSpanSize() = spanCount

//    abstract fun updateSpan(oneHanded: Int, twoHanded: Int)

}