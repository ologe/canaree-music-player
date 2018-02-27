package dev.olog.msc.domain.interactor.song.image

import dev.olog.msc.data.entity.SongImageEntity
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.SongImageGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertSongImageUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: SongImageGateway

) : CompletableUseCaseWithParam<Pair<Song, String>>(schedulers) {

    override fun buildUseCaseObservable(param: Pair<Song, String>): Completable {
        val (song, image) = param

        val albumId = song.albumId
        val songId = song.id

        if (song.album.isNotBlank()){
            return gateway.insert(SongImageEntity(albumId, true, image))
        }

        return gateway.insert(SongImageEntity(songId, false, image))
    }
}