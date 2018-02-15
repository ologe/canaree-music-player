package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.AnimateFavoriteEntity
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

    fun observeToggleFavorite(): Observable<AnimateFavoriteEntity>

    fun toggleLastFavorite()

    fun toggleFavorite(songId: Long)

}