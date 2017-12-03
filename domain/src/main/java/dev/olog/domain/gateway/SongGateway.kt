package dev.olog.domain.gateway

import dev.olog.domain.entity.Song
import io.reactivex.Completable

interface SongGateway : BaseGateway<Song, Long> {

    fun deleteSingle(songId: Long): Completable

    fun deleteGroup(songList: List<Song>): Completable



}