package dev.olog.msc.presentation.library.tab.span.size.lookup


class PlaylistSpanSizeLookup(
        private val isPortrait: Boolean

) : AbsSpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        if (position == 0 || position == 4){
            return spanCount
        }
        return if (isPortrait){
            spanCount / 3
        } else {
            spanCount / 4
        }
    }
}