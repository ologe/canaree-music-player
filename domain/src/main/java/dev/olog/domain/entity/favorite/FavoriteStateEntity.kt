package dev.olog.domain.entity.favorite

data class FavoriteStateEntity(
    val songId: Long,
    val enum: FavoriteEnum,
    val favoriteType: FavoriteType
)

enum class FavoriteEnum {
    FAVORITE,
    NOT_FAVORITE;

    fun reverse(): FavoriteEnum {
        return if (this == FAVORITE) NOT_FAVORITE else FAVORITE
    }
}