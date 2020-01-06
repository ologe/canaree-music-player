package dev.olog.core.entity.favorite

data class FavoriteEntity(
    val songId: Long,
    val enum: FavoriteState,
    val favoriteType: FavoriteTrackType
)

