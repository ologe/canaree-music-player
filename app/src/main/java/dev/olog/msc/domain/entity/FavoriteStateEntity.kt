package dev.olog.msc.domain.entity

data class FavoriteStateEntity(
        val songId: Long,
        val enum: FavoriteEnum,
        val favoriteType: FavoriteType
)

enum class FavoriteEnum {
    FAVORITE,
    NOT_FAVORITE,
    ANIMATE_TO_FAVORITE,
    ANIMATE_NOT_FAVORITE
}