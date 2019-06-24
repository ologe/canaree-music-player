package dev.olog.data.repository

import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.OfflineLyricsEntity
import dev.olog.core.entity.OfflineLyrics
import dev.olog.core.gateway.OfflineLyricsGateway
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class OfflineLyricsRepository @Inject constructor(
        appDatabase: AppDatabase

) : OfflineLyricsGateway {

    private val dao = appDatabase.offlineLyricsDao()

    override fun observeLyrics(id: Long): Observable<String> {
        return dao.observeLyrics(id).toObservable().map {
            if (it.isEmpty()) ""
            else it[0].lyrics
        }
    }

    override fun saveLyrics(offlineLyrics: OfflineLyrics): Completable {
        return Completable.fromCallable {
            dao.saveLyrics(OfflineLyricsEntity(offlineLyrics.trackId, offlineLyrics.lyrics))
        }
    }
}