package dev.olog.msc.appwidgets

class WidgetMetadata(
    @JvmField
    val id: Long,
    @JvmField
    val title: String,
    @JvmField
    val subtitle: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WidgetMetadata

        if (id != other.id) return false
        if (title != other.title) return false
        if (subtitle != other.subtitle) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + subtitle.hashCode()
        return result
    }
}