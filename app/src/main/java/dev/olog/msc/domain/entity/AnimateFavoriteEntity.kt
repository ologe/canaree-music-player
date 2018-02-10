package dev.olog.msc.domain.entity

data class AnimateFavoriteEntity(
        val animateTo: AnimateFavoriteEnum
)

enum class AnimateFavoriteEnum {
    TO_FAVORITE, TO_NOT_FAVORITE
}