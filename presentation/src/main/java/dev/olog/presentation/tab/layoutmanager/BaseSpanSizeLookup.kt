package dev.olog.presentation.tab.layoutmanager

class BaseSpanSizeLookup(
    private val requestedSpanSize: Int

) : AbsSpanSizeLookup() {


    override fun getSpanSize(position: Int): Int {
        return getSpanCount() / requestedSpanSize
    }
}