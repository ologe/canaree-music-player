package dev.olog.feature.library.layout.manager

class PlaylistSpanSizeLookup(
    requestedSpanSize: Int
) : AbsSpanSizeLookup(requestedSpanSize) {

    override fun getSpanSize(position: Int): Int {
        when (position) {
            0, 4 -> return getSpanCount()
        }

        return getSpanCount() / requestedSpanSize
    }

}