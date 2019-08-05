package dev.olog.msc.appwidgets

class WidgetSize(
    @JvmField
    val minWidth: Int,
    @JvmField
    val maxWidth: Int,
    @JvmField
    val minHeight: Int,
    @JvmField
    val maxHeight: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WidgetSize

        if (minWidth != other.minWidth) return false
        if (maxWidth != other.maxWidth) return false
        if (minHeight != other.minHeight) return false
        if (maxHeight != other.maxHeight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = minWidth
        result = 31 * result + maxWidth
        result = 31 * result + minHeight
        result = 31 * result + maxHeight
        return result
    }
}