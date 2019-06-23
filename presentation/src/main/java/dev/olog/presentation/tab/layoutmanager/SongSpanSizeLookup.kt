package dev.olog.presentation.tab.layoutmanager

class SongSpanSizeLookup : AbsSpanSizeLookup() {

    override fun getSpanSize(position: Int): Int = spanCount
}