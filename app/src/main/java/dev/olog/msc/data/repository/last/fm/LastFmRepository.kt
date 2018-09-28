package dev.olog.msc.data.repository.last.fm

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.domain.entity.*
import dev.olog.msc.domain.gateway.LastFmGateway
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LastFmRepository @Inject constructor(
        private val lastFmRepoTrack: LastFmRepoTrack,
        private val lastFmRepoArtist: LastFmRepoArtist,
        private val lastFmRepoAlbum: LastFmRepoAlbum,
        private val lastFmRepoPodcast: LastFmRepoPodcast,
        private val lastFmRepoPodcastAlbum: LastFmRepoPodcastAlbum,
        private val lastFmRepoPodcastArtist: LastFmRepoPodcastArtist

) : LastFmGateway {

    override fun shouldFetchTrack(trackId: Long): Single<Boolean> {
        return lastFmRepoTrack.shouldFetch(trackId)
    }

    override fun getTrack(trackId: Long): Single<Optional<LastFmTrack?>> {
        return lastFmRepoTrack.get(trackId)
    }

    override fun deleteTrack(trackId: Long) {
        lastFmRepoTrack.delete(trackId)
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

    override fun deleteAlbum(albumId: Long) {
        lastFmRepoAlbum.delete(albumId)
    }

    override fun shouldFetchArtist(artistId: Long): Single<Boolean> {
        return lastFmRepoArtist.shouldFetch(artistId)
    }

    override fun getArtist(artistId: Long): Single<Optional<LastFmArtist?>> {
        return lastFmRepoArtist.get(artistId)
    }

    override fun deleteArtist(artistId: Long) {
        lastFmRepoArtist.delete(artistId)
    }

    override fun shouldFetchPodcast(podcastId: Long): Single<Boolean> {
        return lastFmRepoPodcast.shouldFetch(podcastId)
    }

    override fun getPodcast(podcastId: Long): Single<Optional<LastFmPodcast?>> {
        return lastFmRepoPodcast.get(podcastId)
    }

    override fun deletePodcast(podcastId: Long) {
        lastFmRepoPodcast.delete(podcastId)
    }

    override fun shouldFetchPodcastImage(podcastId: Long): Single<Boolean> {
        return lastFmRepoPodcast.getOriginalItem(podcastId)
                .flatMap {
                    Singles.zip(
                            lastFmRepoPodcastAlbum.shouldFetch(it.albumId),
                            lastFmRepoPodcast.shouldFetch(it.id),
                            { isAlbumCached, isPodcastCached -> isAlbumCached || isPodcastCached }
                    )
                }.subscribeOn(Schedulers.io())
    }

    override fun getPodcastImage(podcastId: Long): Single<Optional<String?>> {
        return lastFmRepoPodcast.getOriginalItem(podcastId)
                .flatMap { lastFmRepoPodcastAlbum
                        .get(it.albumId)
                        .map { it.get()!!.image }
                        .map { Optional.of(it) }
                }.onErrorResumeNext {
                    lastFmRepoPodcast
                            .get(podcastId)
                            .map { it.get()!!.image }
                            .map { Optional.of(it) }
                }.subscribeOn(Schedulers.io())
    }

    override fun shouldFetchPodcastAlbum(podcastId: Long): Single<Boolean> {
        return lastFmRepoPodcastAlbum.shouldFetch(podcastId)
    }

    override fun getPodcastAlbum(podcastId: Long): Single<Optional<LastFmPodcastAlbum?>> {
        return lastFmRepoPodcastAlbum.get(podcastId)
    }

    override fun deletePodcastAlbum(podcastId: Long) {
        lastFmRepoAlbum.delete(podcastId)
    }

    override fun shouldFetchPodcastArtist(podcastId: Long): Single<Boolean> {
        return lastFmRepoPodcastArtist.shouldFetch(podcastId)
    }

    override fun getPodcastArtistImage(podcastId: Long): Single<Optional<LastFmPodcastArtist?>> {
        return lastFmRepoPodcastArtist.get(podcastId)
    }

    override fun deleteArtistImage(podcastId: Long) {
        lastFmRepoPodcastArtist.delete(podcastId)
    }
}