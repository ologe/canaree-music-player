package dev.olog.presentation.tab.layoutmanager

import androidx.recyclerview.widget.GridLayoutManager

abstract class AbsSpanSizeLookup : GridLayoutManager.SpanSizeLookup() {

    protected val spanCount = 60

    fun getSpanSize() = spanCount

//    abstract fun updateSpan(oneHanded: Int, twoHanded: Int)

}