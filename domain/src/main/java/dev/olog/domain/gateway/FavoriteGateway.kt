package dev.olog.domain.gateway

import dev.olog.domain.entity.Song
import io.reactivex.Flowable
import io.reactivex.Single

interface FavoriteGateway {

    fun getAll(): Flowable<List<Song>>

    fun addSingle(songId: Long): Single<String>
    fun addGroup(songListId: List<Long>): Single<String>

    fun deleteSingle(songId: Long): Single<String>
    fun deleteGroup(songListId: List<Long>): Single<String>

    fun isFavorite(songId: Long): Single<Boolean>

}