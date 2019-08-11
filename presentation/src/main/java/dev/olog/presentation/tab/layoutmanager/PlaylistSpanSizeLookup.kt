package dev.olog.presentation.tab.layoutmanager

class PlaylistSpanSizeLookup(
    private val requestedSpanSize: Int


) : AbsSpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        when (position) {
            0, 4 -> return getSpanCount()
        }

        return getSpanCount() / requestedSpanSize
    }

}