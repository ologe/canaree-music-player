package dev.olog.msc.presentation.library.tab.span.size.lookup


class PlaylistSpanSizeLookup(
        private val isPortrait: Boolean

) : AbsSpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        when (position){
            0, 4 -> return spanCount
        }

        return if (isPortrait) spanCount / 3 else spanCount / 4
    }
}