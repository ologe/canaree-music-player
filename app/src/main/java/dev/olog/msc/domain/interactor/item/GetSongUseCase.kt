package dev.olog.msc.domain.interactor.item

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.SongGateway
import dev.olog.core.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetSongUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: SongGateway

) : ObservableUseCaseWithParam<Song, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Song> {
        return gateway.observeByParam(mediaId.resolveId).map { it!! }.asObservable()
    }
}
