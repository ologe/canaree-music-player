package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import io.reactivex.Observable

interface HasMostPlayed {

    fun getMostPlayed(mediaId: MediaId): Observable<List<Song>>
    fun insertMostPlayed(mediaId: MediaId): Completable

}