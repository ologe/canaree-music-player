package dev.olog.domain.gateway

import dev.olog.domain.entity.Song
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface FavoriteGateway {

    fun getAll(): Flowable<List<Song>>

    fun addSingle(songId: Long): Completable
    fun addGroup(songListId: List<Long>): Completable

    fun deleteSingle(songId: Long): Completable
    fun deleteGroup(songListId: List<Long>): Completable

    fun isFavorite(songId: Long): Single<Boolean>

}