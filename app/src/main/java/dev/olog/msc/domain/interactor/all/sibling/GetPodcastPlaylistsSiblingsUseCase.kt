package dev.olog.msc.domain.interactor.all.sibling

import dev.olog.core.entity.PodcastPlaylist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PodcastPlaylistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetPodcastPlaylistsSiblingsUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PodcastPlaylistGateway

) : ObservableUseCaseWithParam<List<PodcastPlaylist>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<PodcastPlaylist>> {
        val playlistId = mediaId.resolveId

        return gateway.getAll().map { playlists ->
            playlists.asSequence()
                    .filter { it.id != playlistId } // remove itself
                    .filter { it.size > 0 } // remove empty list
                    .toList()
        }
    }
}
