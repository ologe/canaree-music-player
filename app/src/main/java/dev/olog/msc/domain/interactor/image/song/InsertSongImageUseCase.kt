package dev.olog.msc.domain.interactor.image.song

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class InsertSongImageUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: LastFmGateway

) : CompletableUseCaseWithParam<Pair<Song, String>>(schedulers) {

    override fun buildUseCaseObservable(param: Pair<Song, String>): Completable {
        val (song, image) = param

        val albumId = song.albumId
        val songId = song.id

        if (song.album.isNotBlank()){
            return gateway.insertAlbumImage(albumId, image)
        }

        return gateway.insertTrackImage(songId, image)
    }
}