package dev.olog.msc.presentation.edit.track

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.LastFmTrack
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.UsedImageGateway
import dev.olog.msc.domain.interactor.item.GetUneditedSongUseCase
import dev.olog.msc.domain.interactor.last.fm.GetLastFmTrackUseCase
import dev.olog.msc.domain.interactor.last.fm.LastFmTrackRequest
import dev.olog.msc.utils.MediaId
import io.reactivex.Single
import javax.inject.Inject

class EditTrackFragmentPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val getSongUseCase: GetUneditedSongUseCase,
        private val getLastFmTrackUseCase: GetLastFmTrackUseCase,
        private val usedImageGateway: UsedImageGateway

) {

    private lateinit var originalSong : Song

    fun observeSong(): Single<Song> {
        return getSongUseCase.execute(mediaId)
                .firstOrError()
                .map { it.copy(
                        artist = if (it.artist == AppConstants.UNKNOWN) "" else it.artist,
                        album = if (it.album == AppConstants.UNKNOWN) "" else it.album
                ) }.doOnSuccess {
                    val usedImage = usedImageGateway.getForTrack(it.id)
                            ?: usedImageGateway.getForAlbum(it.albumId)
                            ?: it.image
                    originalSong = it.copy(image = usedImage)
                }
    }

    fun fetchData(): Single<Optional<LastFmTrack?>> {
        return getLastFmTrackUseCase.execute(
                LastFmTrackRequest(originalSong.id, originalSong.title, originalSong.artist, originalSong.album)
        )
    }

    fun getSong(): Song = originalSong

}