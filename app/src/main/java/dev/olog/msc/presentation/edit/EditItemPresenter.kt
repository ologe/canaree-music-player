package dev.olog.msc.presentation.edit

import dev.olog.msc.domain.interactor.last.fm.DeleteLastFmAlbumUseCase
import dev.olog.msc.domain.interactor.last.fm.DeleteLastFmArtistUseCase
import dev.olog.msc.domain.interactor.last.fm.DeleteLastFmTrackUseCase
import dev.olog.msc.domain.interactor.update.UpdateMultipleTracksUseCase
import dev.olog.msc.domain.interactor.update.UpdateTrackUseCase
import io.reactivex.Completable
import org.jaudiotagger.tag.FieldKey
import javax.inject.Inject

class EditItemPresenter @Inject constructor(
        private val deleteTrackUseCase: DeleteLastFmTrackUseCase,
        private val deleteArtistUseCase: DeleteLastFmArtistUseCase,
        private val deleteAlbumUseCase: DeleteLastFmAlbumUseCase,
        private val updateTrackUseCase: UpdateTrackUseCase,
        private val updateMultipleTracksUseCase: UpdateMultipleTracksUseCase

){

    fun deleteTrack(id: Long): Completable {
        return deleteTrackUseCase.execute(id)
    }

    fun deleteAlbum(id: Long): Completable {
        return deleteAlbumUseCase.execute(id)
    }

    fun deleteArtist(id: Long): Completable {
        return deleteArtistUseCase.execute(id)
    }

    fun updateSingle(info: UpdateSongInfo): Completable {
        val albumArtist = if (info.albumArtist.isBlank()) info.artist else info.albumArtist

        return updateTrackUseCase.execute(UpdateTrackUseCase.Data(
                info.originalSong.id,
                info.originalSong.path,
                info.image,
                mapOf(
                        FieldKey.TITLE to info.title,
                        FieldKey.ARTIST to info.artist,
                        FieldKey.ALBUM_ARTIST to albumArtist,
                        FieldKey.ALBUM to info.album,
                        FieldKey.GENRE to info.genre,
                        FieldKey.YEAR to info.year,
                        FieldKey.DISC_NO to info.disc,
                        FieldKey.TRACK to info.track
                )
        ))
    }

    fun updateAlbum(info: UpdateAlbumInfo): Completable {
        val albumArtist = if (info.albumArtist.isBlank()) info.artist else info.albumArtist
        return updateMultipleTracksUseCase.execute(UpdateMultipleTracksUseCase.Data(
                info.mediaId,
                info.image,
                mapOf(
                        FieldKey.ALBUM to info.title,
                        FieldKey.ARTIST to info.artist,
                        FieldKey.ALBUM_ARTIST to albumArtist,
                        FieldKey.GENRE to info.genre,
                        FieldKey.YEAR to info.year
                )
        ))
    }


    fun updateArtist(info: UpdateArtistInfo): Completable {
        val albumArtist = if (info.albumArtist.isBlank()) info.name else info.albumArtist
        return updateMultipleTracksUseCase.execute(UpdateMultipleTracksUseCase.Data(
                info.mediaId,
                info.image,
                mapOf(
                        FieldKey.ARTIST to info.name,
                        FieldKey.ALBUM_ARTIST to albumArtist
                )
        ))
    }

}