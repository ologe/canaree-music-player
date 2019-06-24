package dev.olog.core.gateway

import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.entity.favorite.FavoriteStateEntity
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.entity.track.Song
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface FavoriteGateway {

    fun getAll(): Observable<List<Song>>
    fun getAllPodcasts(): Observable<List<Song>>

    fun addSingle(type: FavoriteType, songId: Long): Completable
    fun addGroup(type: FavoriteType, songListId: List<Long>): Completable

    fun deleteSingle(type: FavoriteType, songId: Long): Completable
    fun deleteGroup(type: FavoriteType, songListId: List<Long>): Completable

    fun deleteAll(type: FavoriteType): Completable

    fun isFavorite(type: FavoriteType, songId: Long): Single<Boolean>

    fun observeToggleFavorite(): Observable<FavoriteEnum>
    fun updateFavoriteState(type: FavoriteType, state: FavoriteStateEntity)

    fun toggleFavorite()

}