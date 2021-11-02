package dev.olog.feature.library.layout.manager

class SongSpanSizeLookup(
    requestedSpanSize: Int
) : AbsSpanSizeLookup(requestedSpanSize) {

    override fun getSpanSize(position: Int): Int = getSpanCount() / requestedSpanSize
}