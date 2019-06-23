package dev.olog.msc.domain.interactor.item

import android.net.Uri
import dev.olog.core.entity.track.Song
import dev.olog.core.executor.IoScheduler
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.domain.interactor.base.SingleUseCaseWithParam
import io.reactivex.Single
import javax.inject.Inject

class GetSongByFileUseCase @Inject internal constructor(
    schedulers: IoScheduler,
    private val gateway: SongGateway

) : SingleUseCaseWithParam<Song, Uri>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(uri: Uri): Single<Song> {
        return gateway.getByUri(uri)
    }
}
