package dev.olog.core.entity.favorite

class FavoriteStateEntity(
    @JvmField
    val songId: Long,
    @JvmField
    val enum: FavoriteEnum,
    @JvmField
    val favoriteType: FavoriteType
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FavoriteStateEntity

        if (songId != other.songId) return false
        if (enum != other.enum) return false
        if (favoriteType != other.favoriteType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = songId.hashCode()
        result = 31 * result + enum.hashCode()
        result = 31 * result + favoriteType.hashCode()
        return result
    }
}

enum class FavoriteEnum {
    FAVORITE,
    NOT_FAVORITE;

    fun reverse(): FavoriteEnum {
        return if (this == FAVORITE) NOT_FAVORITE else FAVORITE
    }
}