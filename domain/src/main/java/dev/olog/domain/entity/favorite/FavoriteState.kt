package dev.olog.domain.entity.favorite

enum class FavoriteState {
    FAVORITE,
    NOT_FAVORITE;

    fun reverse(): FavoriteState {
        return if (this == FAVORITE) NOT_FAVORITE else FAVORITE
    }
}