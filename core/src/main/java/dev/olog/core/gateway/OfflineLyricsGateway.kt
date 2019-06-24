package dev.olog.core.gateway

import dev.olog.core.entity.OfflineLyrics
import io.reactivex.Completable
import io.reactivex.Observable

interface OfflineLyricsGateway {

    fun observeLyrics(id: Long): Observable<String>
    fun saveLyrics(offlineLyrics: OfflineLyrics): Completable

}