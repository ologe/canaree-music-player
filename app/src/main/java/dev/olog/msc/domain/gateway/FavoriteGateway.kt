package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.FavoriteEnum
import dev.olog.msc.domain.entity.FavoriteStateEntity
import dev.olog.msc.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface FavoriteGateway {

    fun getAll(): Observable<List<Song>>

    fun addSingle(songId: Long): Completable
    fun addGroup(songListId: List<Long>): Completable

    fun deleteSingle(songId: Long): Completable
    fun deleteGroup(songListId: List<Long>): Completable

    fun deleteAll(): Completable

    fun isFavorite(songId: Long): Single<Boolean>

    fun observeToggleFavorite(): Observable<FavoriteEnum>
    fun updateFavoriteState(state: FavoriteStateEntity)

    fun toggleFavorite()

}