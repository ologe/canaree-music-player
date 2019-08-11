package dev.olog.presentation.tab.layoutmanager

class SongSpanSizeLookup(
    private val currentSpanSize: Int

) : AbsSpanSizeLookup() {

    override fun getSpanSize(position: Int): Int = getSpanCount() / currentSpanSize
}