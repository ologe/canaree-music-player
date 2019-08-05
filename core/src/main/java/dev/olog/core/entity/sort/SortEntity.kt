package dev.olog.core.entity.sort

class SortEntity(
    @JvmField
    val type: SortType,
    @JvmField
    val arranging: SortArranging
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SortEntity

        if (type != other.type) return false
        if (arranging != other.arranging) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + arranging.hashCode()
        return result
    }
}