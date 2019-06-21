package dev.olog.msc.data.repository.last.fm

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.api.last.fm.annotation.Proxy
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.LastFmPodcastEntity
import dev.olog.msc.data.mapper.LastFmNulls
import dev.olog.msc.data.mapper.toDomain
import dev.olog.msc.data.mapper.toDomainPodcast
import dev.olog.msc.data.mapper.toModel
import dev.olog.msc.domain.entity.LastFmPodcast
import dev.olog.core.entity.Podcast
import dev.olog.msc.domain.gateway.PodcastGateway
import dev.olog.shared.TextUtils
import dev.olog.shared.assertBackgroundThread
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LastFmRepoPodcast @Inject constructor(
        appDatabase: AppDatabase,
        @Proxy private val lastFmService: LastFmService,
        private val gateway: PodcastGateway
) {

    private val dao = appDatabase.lastFmDao()

    fun shouldFetch(podcastId: Long): Single<Boolean> {
        return Single.fromCallable { dao.getPodcast(podcastId) == null }
    }

    fun getOriginalItem(podcastId: Long): Single<Podcast> {
        return gateway.getByParam(podcastId).firstOrError()
    }

    fun get(podcastId: Long): Single<Optional<LastFmPodcast?>> {
        val cachedValue = getFromCache(podcastId)

        val fetch = getOriginalItem(podcastId)
                .flatMap { fetch(it) }
                .map { Optional.of(it) }

        return cachedValue.onErrorResumeNext(fetch)
                .subscribeOn(Schedulers.io())
    }

    private fun getFromCache(trackId: Long): Single<Optional<LastFmPodcast?>> {
        return Single.fromCallable { Optional.ofNullable(dao.getPodcast(trackId)) }
                .map {
                    if (it.isPresent){
                        Optional.of(it.get()!!.toDomain())
                    } else throw NoSuchElementException()
                }
    }

    private fun fetch(podcast: Podcast): Single<LastFmPodcast> {
        assertBackgroundThread()

        val trackId = podcast.id

        val trackTitle = TextUtils.addSpacesToDash(podcast.title)
        val trackArtist = if (podcast.artist == AppConstants.UNKNOWN) "" else podcast.artist

        return lastFmService.getTrackInfo(trackTitle, trackArtist)
                .map { it.toDomainPodcast(trackId) }
                .doOnSuccess { cache(it) }
                .onErrorResumeNext { lastFmService.searchTrack(trackTitle, trackArtist)
                        .map { it.toDomainPodcast(trackId) }
                        .flatMap { result -> lastFmService.getTrackInfo(result.title, result.artist)
                                .map { it.toDomainPodcast(trackId) }
                                .onErrorReturnItem(result)
                        }
                        .doOnSuccess { cache(it) }
                        .onErrorResumeNext {
                            if (it is NoSuchElementException){
                                Single.fromCallable { cacheEmpty(trackId) }
                                        .map { it.toDomain() }
                            } else Single.error(it)
                        }
                }
    }

    private fun cache(model: LastFmPodcast): LastFmPodcastEntity{
        val entity = model.toModel()
        dao.insertPodcast(entity)
        return entity
    }

    private fun cacheEmpty(podcastId: Long): LastFmPodcastEntity{
        val entity = LastFmNulls.createNullPodcast(podcastId)
        dao.insertPodcast(entity)
        return entity
    }

    fun delete(podcastId: Long){
        dao.deletePodcast(podcastId)
    }

}