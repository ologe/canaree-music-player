package dev.olog.feature.tab.layout.manager

class BaseSpanSizeLookup(
    requestedSpanSize: Int
) : AbsSpanSizeLookup(requestedSpanSize) {


    override fun getSpanSize(position: Int): Int {
        return getSpanCount() / requestedSpanSize
    }
}