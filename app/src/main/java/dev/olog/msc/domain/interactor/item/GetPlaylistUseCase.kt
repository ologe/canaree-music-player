package dev.olog.msc.domain.interactor.item

import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetPlaylistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: PlaylistGateway

) : ObservableUseCaseWithParam<Playlist, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Playlist> {
        return gateway.getByParam(mediaId.categoryId)
    }
}
