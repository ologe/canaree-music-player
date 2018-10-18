package dev.olog.msc.presentation.library.tab.span.size.lookup

abstract class AbsSpanSizeLookup : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {

    protected val spanCount = 60

    fun getSpanSize() = spanCount

//    abstract fun updateSpan(oneHanded: Int, twoHanded: Int)

}