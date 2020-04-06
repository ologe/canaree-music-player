package dev.olog.core.entity.favorite

data class FavoriteItemState(
    val songId: Long,
    val enum: FavoriteState,
    val favoriteType: FavoriteTrackType
)

