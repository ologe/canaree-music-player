package dev.olog.feature.library.tab.span

class BaseSpanSizeLookup(
    requestedSpanSize: Int
) : AbsSpanSizeLookup(requestedSpanSize) {


    override fun getSpanSize(position: Int): Int {
        return getSpanCount() / requestedSpanSize
    }
}