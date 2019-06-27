package dev.olog.msc.domain.interactor.search.insert

import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.core.interactor.base.CompletableUseCaseWithParam
import dev.olog.core.MediaId
import io.reactivex.Completable
import javax.inject.Inject


class InsertRecentSearchUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val recentSearchesGateway: RecentSearchesGateway

) : CompletableUseCaseWithParam<MediaId>(scheduler) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Completable {
        val id = mediaId.resolveId
        return when {
            mediaId.isLeaf && !mediaId.isPodcast -> recentSearchesGateway.insertSong(id)
            mediaId.isArtist -> recentSearchesGateway.insertArtist(id)
            mediaId.isAlbum -> recentSearchesGateway.insertAlbum(id)
            mediaId.isPlaylist -> recentSearchesGateway.insertPlaylist(id)
            mediaId.isFolder -> recentSearchesGateway.insertFolder(id)
            mediaId.isGenre -> recentSearchesGateway.insertGenre(id)

            mediaId.isLeaf && mediaId.isPodcast -> recentSearchesGateway.insertPodcast(id)
            mediaId.isPodcastPlaylist -> recentSearchesGateway.insertPodcastPlaylist(id)
            mediaId.isPodcastAlbum -> recentSearchesGateway.insertPodcastAlbum(id)
            mediaId.isPodcastArtist -> recentSearchesGateway.insertPodcastArtist(id)
            else -> throw IllegalArgumentException("invalid category ${mediaId.resolveId}")
        }
    }
}