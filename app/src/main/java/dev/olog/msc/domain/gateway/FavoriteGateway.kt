package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.AnimateFavoriteEntity
import dev.olog.msc.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface FavoriteGateway {

    fun getAll(): Flowable<List<Song>>

    fun addSingle(songId: Long): Single<String>
    fun addGroup(songListId: List<Long>): Single<String>

    fun deleteSingle(songId: Long): Completable
    fun deleteGroup(songListId: List<Long>): Completable

    fun isFavorite(songId: Long): Single<Boolean>

    fun observeToggleFavorite(): Flowable<AnimateFavoriteEntity>

    fun toggleLastFavorite()

    fun toggleFavorite(songId: Long)

}