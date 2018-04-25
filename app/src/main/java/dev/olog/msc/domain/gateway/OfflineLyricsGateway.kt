package dev.olog.msc.domain.gateway

import dev.olog.msc.domain.entity.OfflineLyrics
import io.reactivex.Completable
import io.reactivex.Observable

interface OfflineLyricsGateway {

    fun observeLyrics(id: Long): Observable<String>
    fun saveLyrics(offlineLyrics: OfflineLyrics): Completable

}