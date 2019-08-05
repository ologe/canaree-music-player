package dev.olog.msc.appwidgets

class WidgetActions(
    @JvmField
    val showPrevious: Boolean,
    @JvmField
    val showNext: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WidgetActions

        if (showPrevious != other.showPrevious) return false
        if (showNext != other.showNext) return false

        return true
    }

    override fun hashCode(): Int {
        var result = showPrevious.hashCode()
        result = 31 * result + showNext.hashCode()
        return result
    }
}