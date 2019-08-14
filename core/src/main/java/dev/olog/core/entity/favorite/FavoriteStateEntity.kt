package dev.olog.core.entity.favorite

data class FavoriteStateEntity(
    @JvmField
    val songId: Long,
    @JvmField
    val enum: FavoriteEnum,
    @JvmField
    val favoriteType: FavoriteType
)

enum class FavoriteEnum {
    FAVORITE,
    NOT_FAVORITE;

    fun reverse(): FavoriteEnum {
        return if (this == FAVORITE) NOT_FAVORITE else FAVORITE
    }
}