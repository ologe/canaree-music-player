package dev.olog.domain.entity.favorite

data class FavoriteStateEntity(
    val songId: Long,
    val enum: FavoriteEnum,
    val favoriteType: FavoriteType
) {

    companion object {

        val INVALID: FavoriteStateEntity
            get() = FavoriteStateEntity(
                songId = Long.MIN_VALUE,
                enum = FavoriteEnum.NOT_FAVORITE,
                favoriteType = FavoriteType.TRACK,
            )

    }

}

enum class FavoriteEnum {
    FAVORITE,
    NOT_FAVORITE;

    fun reverse(): FavoriteEnum {
        return if (this == FAVORITE) NOT_FAVORITE else FAVORITE
    }
}