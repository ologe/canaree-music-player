package dev.olog.msc.domain.gateway

import dev.olog.core.entity.track.Song
import dev.olog.core.MediaId
import io.reactivex.Completable
import io.reactivex.Observable

interface HasMostPlayed {

    fun getMostPlayed(mediaId: MediaId): Observable<List<Song>>
    fun insertMostPlayed(mediaId: MediaId): Completable

}