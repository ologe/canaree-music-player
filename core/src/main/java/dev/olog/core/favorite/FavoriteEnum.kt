package dev.olog.core.favorite

// TODO replace with boolean
enum class FavoriteEnum {
    FAVORITE,
    NOT_FAVORITE;

    fun reverse(): FavoriteEnum {
        return if (this == FAVORITE) NOT_FAVORITE else FAVORITE
    }
}