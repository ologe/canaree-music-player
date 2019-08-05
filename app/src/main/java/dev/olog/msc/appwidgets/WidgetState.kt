package dev.olog.msc.appwidgets

class WidgetState(
    @JvmField
    val isPlaying: Boolean
//        val bookmark: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WidgetState

        if (isPlaying != other.isPlaying) return false

        return true
    }

    override fun hashCode(): Int {
        return isPlaying.hashCode()
    }
}