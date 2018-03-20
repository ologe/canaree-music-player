package dev.olog.msc.data.repository.last.fm

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.domain.entity.LastFmAlbum
import dev.olog.msc.domain.entity.LastFmArtist
import dev.olog.msc.domain.entity.LastFmTrack
import dev.olog.msc.domain.gateway.LastFmGateway
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LastFmRepository @Inject constructor(
        private val lastFmRepoTrack: LastFmRepoTrack,
        private val lastFmRepoArtist: LastFmRepoArtist,
        private val lastFmRepoAlbum: LastFmRepoAlbum

) : LastFmGateway {

    override fun shouldFetchTrack(trackId: Long): Single<Boolean> {
        return lastFmRepoTrack.shouldFetch(trackId)
    }

    override fun getTrack(trackId: Long): Single<Optional<LastFmTrack?>> {
        return lastFmRepoTrack.get(trackId)
    }

    override fun shouldFetchTrackImage(trackId: Long): Single<Boolean> {
        return lastFmRepoTrack.getOriginalItem(trackId)
                .flatMap {
                    Singles.zip(
                            lastFmRepoAlbum.shouldFetch(it.albumId),
                            lastFmRepoTrack.shouldFetch(it.id),
                            { isAlbumCached, isTrackCached -> isAlbumCached || isTrackCached }
                    )
                }.subscribeOn(Schedulers.io())
    }

    override fun getTrackImage(trackId: Long): Single<Optional<String?>> {
        return lastFmRepoTrack.getOriginalItem(trackId)
                .flatMap { lastFmRepoAlbum
                        .get(it.albumId)
                        .map { it.get()!!.image }
                        .map { Optional.of(it) }
                }.onErrorResumeNext {
                    lastFmRepoTrack
                            .get(trackId)
                            .map { it.get()!!.image }
                            .map { Optional.of(it) }
                }.subscribeOn(Schedulers.io())
    }

    override fun shouldFetchAlbum(albumId: Long): Single<Boolean> {
        return lastFmRepoAlbum.shouldFetch(albumId)
    }

    override fun getAlbum(albumId: Long): Single<Optional<LastFmAlbum?>> {
        return lastFmRepoAlbum.get(albumId)
    }

    override fun shouldFetchArtist(artistId: Long): Single<Boolean> {
        return lastFmRepoArtist.shouldFetch(artistId)
    }

    override fun getArtist(artistId: Long): Single<Optional<LastFmArtist?>> {
        return lastFmRepoArtist.get(artistId)
    }
}