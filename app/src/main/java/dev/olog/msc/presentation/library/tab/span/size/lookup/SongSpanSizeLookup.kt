package dev.olog.msc.presentation.library.tab.span.size.lookup

class SongSpanSizeLookup : AbsSpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        return spanCount
    }
}