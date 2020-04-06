package dev.olog.domain.entity.favorite

data class FavoriteItemState(
    val songId: Long,
    val enum: FavoriteState,
    val favoriteType: FavoriteTrackType
)

