package dev.olog.msc.domain.interactor.all.sibling

import dev.olog.msc.constants.PlaylistConstants
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetPlaylistSiblingsUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PlaylistGateway

) : ObservableUseCaseUseCaseWithParam<List<Playlist>, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<List<Playlist>> {
        val playlistId = mediaId.categoryValue.toLong()

        val observable = if (PlaylistConstants.isAutoPlaylist(playlistId)){
            gateway.getAllAutoPlaylists()
        } else gateway.getAll()

        return observable.map {
            it.asSequence()
                    .filter { it.id != playlistId } // remove itself
                    .filter { it.size > 0 } // remove empty list
                    .toList()
        }
    }
}
