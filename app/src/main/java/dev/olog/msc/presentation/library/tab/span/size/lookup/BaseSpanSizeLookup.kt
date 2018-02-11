package dev.olog.msc.presentation.library.tab.span.size.lookup

class BaseSpanSizeLookup(
        private val isPortrait: Boolean

) : AbsSpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        return if (isPortrait) {
            spanCount / 3
        } else {
            spanCount / 4
        }
    }
}