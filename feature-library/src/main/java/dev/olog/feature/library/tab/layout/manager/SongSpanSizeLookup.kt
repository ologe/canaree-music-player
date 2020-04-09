package dev.olog.feature.library.tab.layout.manager

internal class SongSpanSizeLookup(
    requestedSpanSize: Int
) : AbsSpanSizeLookup(requestedSpanSize) {

    override fun getSpanSize(position: Int): Int = getSpanCount() / requestedSpanSize
}