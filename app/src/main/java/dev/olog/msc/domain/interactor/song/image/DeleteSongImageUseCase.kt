package dev.olog.msc.domain.interactor.song.image

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.SongImageGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class DeleteSongImageUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: SongImageGateway

) : CompletableUseCaseWithParam<Song>(schedulers) {

    override fun buildUseCaseObservable(param: Song): Completable {
        val albumId = param.albumId
        val songId = param.id

        if (param.album.isNotBlank()){
            return gateway.delete(albumId)
                    .andThen(gateway.delete(songId))
        }

        return gateway.delete(songId)
    }
}